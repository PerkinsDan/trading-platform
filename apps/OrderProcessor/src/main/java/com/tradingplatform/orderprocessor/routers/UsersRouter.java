package com.tradingplatform.orderprocessor.routers;

import com.mongodb.client.model.Filters;
import com.tradingplatform.orderprocessor.database.MongoClientConnection;
import com.tradingplatform.orderprocessor.validations.Validation;
import com.tradingplatform.orderprocessor.validations.ValidationBuilder;
import com.tradingplatform.orderprocessor.validations.ValidationResult;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.Document;

import static com.tradingplatform.orderprocessor.database.DatabaseUtils.*;

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

        Validation validation = new ValidationBuilder()
                                    .validateAttribute("userId")
                                    .build();

        ValidationResult result = validation.validate(body);

        try{
            if (result.isValid){
              var usersCollection = MongoClientConnection.getCollection("users");

              Document newUserDoc = new Document()
              .append("userId", body.getString("userId"))
              .append("balance", 0)
              .append("portfolio", Collections.emptyList());

              usersCollection.insertOne(newUserDoc);

              ctx
                .response()
                .setStatusCode(200)
                .putHeader("Content-Type", "application/json")
                .end("User created successfully");

          } else {
            ctx
              .response()
              .setStatusCode(400)
              .putHeader("Content-Type", "application/json")
              .end("Error while creating user : " + result.errorMessage);    
          }
        }
        catch( Exception e){
          e.printStackTrace();
          ctx.response().
          setStatusCode(500).
          putHeader("Content-Type", "application/json").
          end("Internal Server Error : Unexpected error while creating user - user could not be created.");
        }
 
      });

    router
      .post("/update-balance")
      .handler(BodyHandler.create())
      .handler(ctx -> {
        JsonObject body = ctx.body().asJsonObject();

        Validation validation = new ValidationBuilder()
                                    .validateUserId()
                                    .validateAttribute("moneyAddedToBalance")
                                    .build();
        ValidationResult result = validation.validate(body);
        try{
          if (result.isValid){
            creditUser(body.getString("userId"), body.getDouble("moneyAddedToBalance"));
            ctx
            .response()
            .setStatusCode(200)
            .putHeader("Content-Type", "application/json")
            .end("Balance updated successfully");
          } else {
            ctx
            .response()
            .setStatusCode(400)
            .putHeader("Content-Type", "application/json")
            .end("Error while updating user balance: " + result.errorMessage); 

          }
        }
        catch(Exception e){
          e.printStackTrace();
          ctx.response().
          setStatusCode(500).
          putHeader("Content-Type", "application/json").
          end("Internal Server Error : Unexpected error while adding balance - balance was not updated.");
        }
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
