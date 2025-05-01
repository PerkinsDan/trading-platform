package com.tradingplatform.orderprocessor.routers;

import static com.tradingplatform.orderprocessor.database.DatabaseUtils.*;
import static com.tradingplatform.orderprocessor.orders.OrderType.BUY;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.tradingplatform.orderprocessor.OrderProcessorService;
import com.tradingplatform.orderprocessor.database.MongoClientConnection;
import com.tradingplatform.orderprocessor.orders.Order;
import com.tradingplatform.orderprocessor.orders.OrderType;
import com.tradingplatform.orderprocessor.orders.Ticker;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import java.util.ArrayList;
import org.bson.Document;

public class OrdersRouter {

  private final Router router;
  private final OrderProcessorService orderProcessorService;

  OrdersRouter(Vertx vertx) {
    router = Router.router(vertx);
    orderProcessorService = OrderProcessorService.getInstance();

    initialise();
  }

  public static Order createOrder(JsonObject body) {
    String typeStr = body.getString("type");
    String tickerStr = body.getString("ticker");
    double price = body.getDouble("price");
    int quantity = body.getInteger("quantity");
    String userId = body.getString("userId");

    OrderType type = OrderType.valueOf(typeStr);
    Ticker ticker = Ticker.valueOf(tickerStr);

    return new Order(type, userId, ticker, price, quantity);
  }

  public Router getRouter() {
    return router;
  }

  void initialise() {
    router
      .post("/create")
      .handler(BodyHandler.create())
      .handler(ctx -> {
        JsonObject body = ctx.body().asJsonObject();

        String validationResult = passValidations(body);

        if (!validationResult.equals("PASSED VALIDATIONS")) {
          ctx
            .response()
            .setStatusCode(422)
            .putHeader("Content-Type", "application/json")
            .end(new JsonObject().put("Error", validationResult).encode());

          return;
        }

        Order order = createOrder(body);

        MongoCollection<Document> activeOrdersCollection =
          MongoClientConnection.getCollection("activeOrders");

        Document orderDoc = order.toDoc();
        activeOrdersCollection.insertOne(orderDoc);

        ArrayList<String> matchesFound = orderProcessorService.processOrder(
          order
        );

        if (!matchesFound.isEmpty()) {
          updateCollectionsWithMatches(matchesFound);
        }

        MongoCollection<Document> usersCollection =
                MongoClientConnection.getCollection("users");

        String userId = body.getString("userId");
        double price = body.getDouble("price");
        int quantity = body.getInteger("quantity");

        if(order.getType() == BUY) {
            usersCollection.updateOne(
                    Filters.eq("userId", userId),
                    new Document("$inc", new Document("balance", -price * quantity))
            );
        }

        ctx
          .response()
          .setStatusCode(200)
          .putHeader("Content-Type", "application/json")
          .end("Order created");
      });

    router
      .post("/cancel")
      .handler(BodyHandler.create())
      .handler(ctx -> {
        JsonObject body = ctx.body().asJsonObject();

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

        if (orderJson == null) {
          ctx
            .response()
            .setStatusCode(404)
            .putHeader("Content-Type", "application/json")
            .end("Order cannot be cancelled... Order does not exist");

          return;
        }

        Order order = createOrder(orderJson);

        orderProcessorService.cancelOrder(orderId, ticker, type);

        activeOrdersCollection.deleteOne(Filters.eq("orderId", orderId));

        if (order.getType() == OrderType.BUY) {
          double amountToCreditBack = order.getPrice() * order.getQuantity();

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

        ctx.response().setStatusCode(200);
      });
  }
}
