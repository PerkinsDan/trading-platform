package org.example;

import com.mongodb.client.model.Filters;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import orderProcessor.Order;
import org.bson.Document;
import org.database.DatabaseUtils;
import org.bson.conversions.Bson;
import org.database.MongoClientConnection;

import java.util.ArrayList;

public class MainRouter {

    Router router;

    MainRouter(Vertx vertx) {
        router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
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
//make post
        router.get("/create-user").handler(ctx -> {
            //UUID
            //if new user we set balance to 0
            JsonObject body = ctx.getBodyAsJson();

            String userID = body.getString("userID");
            Integer balance = body.getInteger("balance");

            Document newUserDoc = new Document()
                    .append("userID", userID)
                    .append("balance", balance);

            var usersCollection = MongoClientConnection.getCollection("users");
            usersCollection.insertOne(newUserDoc);

            ctx.response().end("Creating user...");
        });

        router.get("/after-trade-update-user-balance").handler(ctx -> {
            //user has a starting balance
            //when a buy or sell order is made that is associated with their account
            //update balance to reflect it
            ctx.response().end("Updating user's balance...");
        });

        router.get("/user-active-positions").handler(ctx -> {
            String userID = ctx.request().getParam("userID");
            if (userID == null) {
                ctx.response().setStatusCode(400).end("Missing userID query parameter");
            }

            var usersCollection = MongoClientConnection.getCollection("users");

            Bson userID_filter = Filters.eq("userID", userID);
            var userDocuments = new ArrayList<JsonObject>();
            usersCollection.find(userID_filter).forEach(
                    doc -> userDocuments.add(new JsonObject(doc.toJson()))
            );

            ctx.response()
                    .putHeader("Content-Type", "application/json")
                    .end(userDocuments.toString());
        });


        router.get("/user-trade-history").handler(ctx -> {
            // logic to retrieve user's trade history
            ctx.response().end("Retrieving user's trade history...");
        });

        router.post("/create-order").handler(ctx -> {
            JsonObject body = ctx.getBodyAsJson();
            Order order = DatabaseUtils.createOrderAndInsertIntoDatabase(body);
            DatabaseUtils.processOrder(order);

            ctx.response()
                    .setStatusCode(201)
                    .putHeader("Content-Type", "application/json")
                    .end("Order created");
        });

    }
}


//create-order
//create-user

//user-active-positions
//user-trade-history
//orders
//update-user-balance
//get-user-account (to get balance)
