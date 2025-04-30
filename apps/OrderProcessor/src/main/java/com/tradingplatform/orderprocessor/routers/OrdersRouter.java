package com.tradingplatform.orderprocessor.routers;

import static com.tradingplatform.orderprocessor.database.DatabaseUtils.*;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.tradingplatform.orderprocessor.OrderProcessorService;
import com.tradingplatform.orderprocessor.database.MongoClientConnection;
import com.tradingplatform.orderprocessor.orders.Order;
import com.tradingplatform.orderprocessor.orders.OrderType;
import com.tradingplatform.orderprocessor.orders.Ticker;
import com.tradingplatform.orderprocessor.validations.Validation;
import com.tradingplatform.orderprocessor.validations.ValidationBuilder;
import com.tradingplatform.orderprocessor.validations.ValidationResult;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
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

          MongoCollection<Document> activeOrdersCollection =
                  MongoClientConnection.getCollection("activeOrders");
          MongoCollection<Document> orderHistoryCollection =
                  MongoClientConnection.getCollection("orderHistory");
          MongoCollection<Document> usersCollection =
                  MongoClientConnection.getCollection("users");

          //Create  and use POC validationBuilder object, we might wanna have validaions that
          //check that the price and quantity are valid too.
          Validation validation = new ValidationBuilder().
                                      validateUserId().
                                      validateOrderType().
                                      validateTicker().
                                      validateUserBalanceAndPorfolio().
                                      build();
        
          ValidationResult result = validation.validate(body);

          try {
              if (result.isValid){
                      //unchanged logic from previous create-order/ endpoint
                      // Order order = DatabaseUtils.createOrder(body);

                      // insertOrderIntoDatabase(
                      //         order,
                      //         activeOrdersCollection
                      // );

                      // OrderProcessorService orderProcessor = OrderProcessorService.getInstance();

                      // ArrayList<Document> matchesFoundAsMongoDBDocs =
                      //         DatabaseUtils.processOrderAndParseMatchesFound(
                      //                 order,
                      //                 orderProcessor
                      //         );

                      // if (!matchesFoundAsMongoDBDocs.isEmpty()) {
                      //   matchesFoundAsMongoDBDocs.replaceAll(doc -> doc.toJson());
                      //   updateCollectionsWithMatches(matchesFoundAsMongoDBDocs);
               
                      // }

                      ctx.response().
                              setStatusCode(201).
                              putHeader("Content-Type", "application/json").
                              end("Order created");
              } else {
                      ctx.response().
                      setStatusCode(400).
                      putHeader("Content-Type", "application/json").
                      end("Error - Validation Error : " + result.errorMessage);
              }
          } catch (Exception e) {
              e.printStackTrace();
              ctx.response().
              setStatusCode(500).
              putHeader("Content-Type", "application/json").
              end("Internal Server Error : if were here sthg has gone quite wrong");
          }
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

        ctx.response().setStatusCode(200);
      });
  }
}
