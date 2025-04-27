package com.tradingplatform.orderprocessor.routers;

import static com.tradingplatform.orderprocessor.database.DatabaseUtils.*;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.tradingplatform.orderprocessor.OrderProcessorService;
import com.tradingplatform.orderprocessor.database.DatabaseUtils;
import com.tradingplatform.orderprocessor.database.MongoClientConnection;
import com.tradingplatform.orderprocessor.orders.Order;
import com.tradingplatform.orderprocessor.orders.OrderType;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import java.util.ArrayList;
import org.bson.Document;

public class OrdersRouter {

  private final Router router;
  private final OrderProcessorService orderProcessorService;

  public Router getRouter() {
    return router;
  }

  OrdersRouter(Vertx vertx) {
    router = Router.router(vertx);
    orderProcessorService = OrderProcessorService.getInstance();

    initialise();
  }

  void initialise() {
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

        ArrayList<String> matchesFound = orderProcessorService.processOrder(
          order
        );

        if (!matchesFound.isEmpty()) {
          updateDb(
            matchesFound,
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

        orderProcessorService.cancelOrder(orderId, ticker, type);

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
  }
}
