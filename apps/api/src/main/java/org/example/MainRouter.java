package org.example;

import com.mongodb.client.model.Filters;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import orderProcessor.Order;
import orderProcessor.OrderProcessor;
import orderProcessor.Ticker;
import orderProcessor.OrderType;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.database.MongoClientConnection;

import java.util.ArrayList;

public class MainRouter {

    Router router;
    OrderProcessor orderprocessor = OrderProcessor.getInstance();

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

            String typeStr = body.getString("type");
            String tickerStr = body.getString("ticker");
            double price = body.getDouble("price");
            int quantity = body.getInteger("quantity");

            OrderType type = OrderType.valueOf(typeStr);
            Ticker ticker = Ticker.valueOf(tickerStr);

            Document newOrderDoc = new Document()
                    .append("type", type)
                    .append("ticker", ticker)
                    .append("price", price)
                    .append("quantity", quantity);

            var ordersCollection = MongoClientConnection.getCollection("orders");
            ordersCollection.insertOne(newOrderDoc);

            Order order = new Order(type, ticker, price, quantity);
            ArrayList<Document> matchesFound = orderprocessor.processOrder(order);

            if (!matchesFound.isEmpty()) {

                if(matchesFound.get(0).getBoolean("filled")){
                    ordersCollection.deleteOne(Filters.eq("orderId", matchesFound.get(0).getString("orderId")));
                }

                if(!matchesFound.get(0).getBoolean("filled")){
                    String orderId = matchesFound.get(0).getString("orderId");
                    int quantityChange = matchesFound.get(0).getInteger("quantityChange");

                    ordersCollection.findOneAndUpdate(
                            Filters.eq("orderId", orderId),
                            new Document("$inc", new Document("quantity", quantityChange))
                    );
                }

                if(matchesFound.get(1).getBoolean("filled")){
                    ordersCollection.deleteOne(Filters.eq("orderId", matchesFound.get(0).getString("orderId")));
                }

                if(!matchesFound.get(1).getBoolean("filled")){
                    String orderId = matchesFound.get(1).getString("orderId");
                    int quantityChange = matchesFound.get(1).getInteger("quantityChange");

                    ordersCollection.findOneAndUpdate(
                            Filters.eq("orderId", orderId),
                            new Document("$inc", new Document("quantity", -quantityChange))
                    );
                }
            }

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
//market-prices
