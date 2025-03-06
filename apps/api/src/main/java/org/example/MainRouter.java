package org.example;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.bson.Document;
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

        router.get("/user-active-positions").handler(ctx -> {
            // logic to retrieve user's active trade positions
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

            var ordersCollection = MongoClientConnection.getOrdersCollection();
            ordersCollection.insertOne(newOrderDoc);

            Order order = new Order(type, ticker, price, quantity);
            OrderProcessor.addOrder(order);

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
