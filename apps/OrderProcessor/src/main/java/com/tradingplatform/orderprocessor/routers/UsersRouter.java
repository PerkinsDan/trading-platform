package com.tradingplatform.orderprocessor.routers;

import com.mongodb.client.model.Filters;
import com.tradingplatform.orderprocessor.database.MongoClientConnection;
import com.tradingplatform.orderprocessor.orders.Ticker;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bson.Document;

public class UsersRouter {

  private final Router router;

  UsersRouter(Vertx vertx) {
    router = Router.router(vertx);
    setupRoutes();
  }

  public Router getRouter() {
    return router;
  }

  private void setupRoutes() {
    router
      .post("/create")
      .handler(BodyHandler.create())
      .handler(ctx -> {
        JsonObject body = ctx.body().asJsonObject();
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
          .append("balance", 0.0)
          .append("portfolio", Collections.emptyList());

        usersCollection.insertOne(newUserDoc);

        ctx.response().end("User created successfully.");
      });

    router
      .post("/update-balance")
      .handler(BodyHandler.create())
      .handler(ctx -> {
        JsonObject body = ctx.body().asJsonObject();
        if (
          body == null ||
          !body.containsKey("userId") ||
          !body.containsKey("moneyAddedToBalance")
        ) {
          ctx.response().setStatusCode(400).end("Invalid request body");
          return;
        }
        String userId = body.getString("userId");
        double moneyAddedToBalance = body.getDouble("moneyAddedToBalance");
        var usersCollection = MongoClientConnection.getCollection("users");
        usersCollection.updateOne(
          Filters.eq("userId", userId),
          new Document("$inc", new Document("balance", moneyAddedToBalance))
        );
        ctx.response().end("User balance updated successfully");
      });

    router
      .get("/active-positions")
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
      .get("/account")
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

        double balance = userDoc.getDouble("balance");
        JsonArray portfolio = new JsonArray(
          userDoc.getList("portfolio", Document.class, Collections.emptyList())
        );

        JsonObject response = new JsonObject()
          .put("userId", userId)
          .put("balance", balance)
          .put("portfolio", portfolio);

        ctx
          .response()
          .putHeader("Content-Type", "application/json")
          .end(response.encode());
      });

    router
      .get("/trade-history")
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
