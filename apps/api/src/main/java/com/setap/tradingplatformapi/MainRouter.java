package com.setap.tradingplatformapi;

import static com.setap.tradingplatformapi.database.DatabaseUtils.updateDBToReflectFulfilledOrders;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.setap.tradingplatformapi.database.DatabaseUtils;
import com.setap.tradingplatformapi.database.MongoClientConnection;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import java.util.ArrayList;
import orderProcessor.Order;
import orderProcessor.OrderProcessor;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.setap.tradingplatformapi.database.DatabaseUtils.updateDb;

public class MainRouter {

  Router router;

  MainRouter(Vertx vertx) {
    router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router
      .route()
      .handler(
        CorsHandler.create("*") // Allow all origins
          .allowedMethod(io.vertx.core.http.HttpMethod.GET)
          .allowedMethod(io.vertx.core.http.HttpMethod.POST)
          .allowedMethod(io.vertx.core.http.HttpMethod.PUT)
          .allowedMethod(io.vertx.core.http.HttpMethod.DELETE)
          .allowedHeader("Content-Type")
          .allowedHeader("Authorization")
      );
    initialise();
  }

  void initialise() {
    router
      .route("/")
      .handler(ctx -> {
        HttpServerResponse response = ctx.response();
        response.putHeader("content-type", "text/plain");
        response.end("Hello World from Vert.x-Web!");
      });

    router
      .post("/create-user")
      .handler(ctx -> {
        JsonObject body = ctx.getBodyAsJson();
        String userId = body.getString("userId");

        var usersCollection = MongoClientConnection.getCollection("users");
        Document existingUser = usersCollection
          .find(Filters.eq("userId", userId))
          .first();

        if (existingUser != null) {
          ctx.response().setStatusCode(409).end("User already exists");
          return;
        }

        Document newUserDoc = new Document()
          .append("userId", userId)
          .append("balance", 0);

        usersCollection.insertOne(newUserDoc);

        ctx.response().end("User created successfully.");
      });

    router
      .post("/update-user-balance")
      .handler(ctx -> {
        JsonObject body = ctx.getBodyAsJson();
        if (
          body == null ||
          !body.containsKey("userId") ||
          !body.containsKey("moneyAddedToBalance")
        ) {
          ctx.response().setStatusCode(400).end("Invalid request body");
          return;
        }
        String userId = body.getString("userId");
        int moneyAddedToBalance = body.getInteger("moneyAddedToBalance");
        var usersCollection = MongoClientConnection.getCollection("users");
        usersCollection.updateOne(
          Filters.eq("userId", userId),
          new Document("$inc", new Document("balance", moneyAddedToBalance))
        );
        ctx.response().end("User balance updated successfully");
      });

    router
      .get("/user-active-positions")
      .handler(ctx -> {
        String userId = ctx.request().getParam("userId");
        if (userId == null) {
          ctx
            .response()
            .setStatusCode(400)
            .end("Missing userId query parameter");
        }

        var usersCollection = MongoClientConnection.getCollection("users");

        Bson userId_filter = Filters.eq("userId", userId);
        var userDocuments = new ArrayList<JsonObject>();
        usersCollection
          .find(userId_filter)
          .forEach(doc -> userDocuments.add(new JsonObject(doc.toJson())));

        ctx
          .response()
          .putHeader("Content-Type", "application/json")
          .end(userDocuments.toString());
      });

    router
      .get("/user-trade-history")
      .handler(ctx -> {
        String userId = ctx.request().getParam("userId");
        if (userId == null) {
          ctx
            .response()
            .setStatusCode(400)
            .end("Missing userId query parameter");
          return;
        }

        // TODO: Update this for the actual trade history
        // Placeholder logic for trade history
        var tradeHistory = new ArrayList<JsonObject>();

        ctx
          .response()
          .putHeader("Content-Type", "application/json")
          .end(new JsonObject().put("tradeHistory", tradeHistory).encode());
      });

    router
      .post("/create-order")
      .handler(ctx -> {
        JsonObject body = ctx.getBodyAsJson();

                    MongoCollection<Document> activeOrdersCollection = MongoClientConnection.getCollection("activeOrders");
                    MongoCollection<Document> orderHistoryCollection = MongoClientConnection.getCollection("orderHistory");
                    MongoCollection<Document> usersCollection = MongoClientConnection.getCollection("users");


                    Order order = DatabaseUtils.createOrderAndInsertIntoDatabase(body, activeOrdersCollection);
                    OrderProcessor orderProcessor = OrderProcessor.getInstance();

        ArrayList<Document> matchesFoundAsMongoDBDocs =
          DatabaseUtils.processOrderAndParseMatchesFound(order, orderProcessor);

        if (!matchesFoundAsMongoDBDocs.isEmpty()) {
          updateDBToReflectFulfilledOrders(
            matchesFoundAsMongoDBDocs,
            ordersCollection,
            usersCollection
          );
        }

        ctx
          .response()
          .setStatusCode(201)
          .putHeader("Content-Type", "application/json")
          .end("Order created");
      });

    router
      .get("/user-account")
      .handler(ctx -> {
        String userId = ctx.request().getParam("userId");
        if (userId == null) {
          ctx
            .response()
            .setStatusCode(400)
            .end("Missing userId query parameter");
          return;
        }

        var usersCollection = MongoClientConnection.getCollection("users");
        Document userDoc = usersCollection
          .find(Filters.eq("userId", userId))
          .first();

        if (userDoc == null) {
          ctx.response().setStatusCode(404).end("User not found");
          return;
        }

        int balance = userDoc.getInteger("balance", 0);
        JsonObject response = new JsonObject()
          .put("userId", userId)
          .put("balance", balance);

        ctx
          .response()
          .putHeader("Content-Type", "application/json")
          .end(response.encode());
      });
  }
}
//create-order
//create-user
//user-active-positions
//user-trade-history
//orders
//update-user-balance
//user-account (to get balance)
