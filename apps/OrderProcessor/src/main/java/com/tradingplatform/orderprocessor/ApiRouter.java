package com.tradingplatform.orderprocessor;

import static com.tradingplatform.orderprocessor.database.DatabaseUtils.*;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.tradingplatform.orderprocessor.database.DatabaseUtils;
import com.tradingplatform.orderprocessor.database.MongoClientConnection;
import com.tradingplatform.orderprocessor.orders.Order;
import com.tradingplatform.orderprocessor.orders.OrderProcessor;
import com.tradingplatform.orderprocessor.orders.OrderType;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bson.Document;

public class ApiRouter {

  Router router;

  ApiRouter(Vertx vertx) {
    router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router
      .route()
      .handler(
        CorsHandler.create("*")
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
          .append("balance", 0)
          .append("portfolio", Collections.emptyList());

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
          return;
        }

        var activeOrdersCollection = MongoClientConnection.getCollection(
          "activeOrders"
        );

        ArrayList<JsonObject> activeOrders = new ArrayList<>();

        activeOrdersCollection
          .find(Filters.eq("userId", userId))
          .forEach(doc -> activeOrders.add(new JsonObject(doc.toJson())));

        ctx
          .response()
          .putHeader("Content-Type", "application/json")
          .end(activeOrders.toString());
      });

    router
      .post("/create-order")
      .handler(ctx -> {
        JsonObject body = ctx.getBodyAsJson();

        MongoCollection<Document> activeOrdersCollection =
          MongoClientConnection.getCollection("activeOrders");
        MongoCollection<Document> orderHistoryCollection =
          MongoClientConnection.getCollection("orderHistory");
        MongoCollection<Document> usersCollection =
          MongoClientConnection.getCollection("users");

        String validationResult = passValidations(body, usersCollection);

        if (!validationResult.equals("PASSED VALIDATIONS")) {
          ctx
            .response()
            .setStatusCode(422)
            .putHeader("Content-Type", "application/json")
            .end(new JsonObject().put("Error", validationResult).encode());
          return;
        }

        Order order = DatabaseUtils.createOrder(body);

        insertOrderIntoDatabase(order, activeOrdersCollection);

        OrderProcessor orderProcessor = OrderProcessor.getInstance();

        ArrayList<Document> matchesFoundAsMongoDBDocs =
          DatabaseUtils.processOrderAndParseMatchesFound(order, orderProcessor);

        if (!matchesFoundAsMongoDBDocs.isEmpty()) {
          updateDb(
            matchesFoundAsMongoDBDocs,
            activeOrdersCollection,
            usersCollection,
            orderHistoryCollection
          );
        }

        ctx
          .response()
          .setStatusCode(201)
          .putHeader("Content-Type", "application/json")
          .end("Order created");
      });

    router
      .post("/cancel-order")
      .handler(ctx -> {
        JsonObject body = ctx.getBodyAsJson();

        String orderId = body.getString("orderId");
        String userId = body.getString("userId");
        String ticker = body.getString("ticker");
        String type = body.getString("type");

        var activeOrdersCollection = MongoClientConnection.getCollection(
          "activeOrders"
        );

        var orderHistoryCollection = MongoClientConnection.getCollection(
          "orderHistory"
        );

        var usersCollection = MongoClientConnection.getCollection("users");

        JsonObject orderJson = JsonObject.mapFrom(
          activeOrdersCollection.find(Filters.eq("orderId", orderId)).first()
        );

        Order order = DatabaseUtils.createOrder(orderJson);

        OrderProcessor orderProcessor = OrderProcessor.getInstance();
        orderProcessor.cancelOrder(orderId, ticker, type);

        activeOrdersCollection.deleteOne(Filters.eq("orderId", orderId));

        if (order.getType() == OrderType.BUY) {
          int amountToCreditBack = (int) order.getPrice() * order.getQuantity();

          usersCollection.updateOne(
            Filters.eq("userId", userId),
            new Document("$inc", new Document("balance", amountToCreditBack))
          );
        }

        if (previouslyPartiallyFilled(orderId)) {
          orderHistoryCollection.updateOne(
            Filters.eq("orderId", orderId),
            new Document("$set", new Document("cancelled", true))
          );
        } else {
          Document orderDoc = order.toDoc();
          orderDoc.put("cancelled", true);

          orderHistoryCollection.insertOne(orderDoc);
        }

        ctx
          .response()
          .setStatusCode(201)
          .putHeader("Content-Type", "application/json")
          .end("Order cancelled");
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
          ctx.response().setStatusCode(204).end("User not found");
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

        var orderHistoryCollection = MongoClientConnection.getCollection(
          "orderHistory"
        );

        List<JsonObject> history = new ArrayList<>();

        orderHistoryCollection
          .find(Filters.eq("userId", userId))
          .forEach(doc -> history.add(new JsonObject(doc.toJson())));

        if (history.isEmpty()) {
          ctx.response().setStatusCode(204).end("No history found");
          return;
        }

        ctx
          .response()
          .putHeader("Content-Type", "application/json")
          .end(history.toString());
      });
  }
}
