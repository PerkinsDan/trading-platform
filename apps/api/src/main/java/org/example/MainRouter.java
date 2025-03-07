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

        router.get("/create-user").handler(ctx -> {
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

        router.get("/update-user-balance").handler(ctx -> {
            // logic to retrieve user's trade history
            ctx.response().end("Updating user's balance...");
        });

        router.get("/user-active-positions").handler(ctx -> {
            //receive user parameters to query on
            JsonObject body = ctx.getBodyAsJson();
            String userID = body.getString("userID");
            var usersCollection = MongoClientConnection.getCollection("users");

            //query database for user object
            Bson filter = Filters.gt("userID", userID);
            usersCollection.find(filter).forEach(doc -> System.out.println(doc.toJson()));
            //usersCollection.find(filter).forEach(doc -> );
            //TODO: RETURN ATTRIBUTES THAT CAN BE USED TO CREATE USER OBJECT, SIMILAR TO .APPEND IN /CREATE-ORDER

            //return it as user object

            ctx.response().end("Retrieving user's active positions...");
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


//user-active-positions
//user-trade-history
//orders
//create-order
//create-user
//update-user-balance
//get-user-account (to get balance)
