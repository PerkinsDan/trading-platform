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

          //Create  and use POC validationBuilder object, we might wanna have validaions that
          //check that the price and quantity are valid too.
          Validation validation = new ValidationBuilder().
                                      validateQuantity().
                                      validateDouble("price").
                                      validateOrderType().
                                      validateTicker().
                                      validateUserId().
                                      validateUserBalanceAndPorfolio().
                                      build();
        
          ValidationResult result = validation.validate(body);

          try {
              if (result.isValid){
                      Order order = createOrder(body);

                      Document orderDoc = order.toDoc();
                      activeOrdersCollection.insertOne(orderDoc);
              
                      ArrayList<String> matchesFound = orderProcessorService.processOrder(
                        order
                      );
              
                      if (!matchesFound.isEmpty()) {
                        updateCollectionsWithMatches(matchesFound);
                      }
              
                      ctx
                        .response()
                        .setStatusCode(200)
                        .putHeader("Content-Type", "application/json")
                        .end("Order created");
                      return;
              } else {
                      ctx.response().
                      setStatusCode(400).
                      putHeader("Content-Type", "application/json").
                      end("Error - Validation Error : " + result.errorMessage);
                      return;
              }
          } catch (Exception e) {
              e.printStackTrace();
              ctx.response().
              setStatusCode(500).
              putHeader("Content-Type", "application/json").
              end("Internal Server Error : Unexpected error while creating order - Order could not be placed.");
              return;
          }
      });

    router
      .post("/cancel")
      .handler(BodyHandler.create())
      .handler(ctx -> {
        JsonObject body = ctx.body().asJsonObject();

        Validation validation = new ValidationBuilder()
                                    .validateTicker()
                                    .validateOrderType()
                                    .validateOrderId()
                                    .validateUserId()
                                    .validateOrderToCancelBelongsToUser()
                                    .build();

        ValidationResult result = validation.validate(body);
        try{
            if (result.isValid){

              var activeOrdersCollection = MongoClientConnection.getCollection(
                "activeOrders"
              );
              
              //Golden source of information
              Document orderDoc = activeOrdersCollection
              .find(Filters.eq("orderId", body.getString("orderId")))
              .first();

              String orderId = orderDoc.getString("orderId");
              String userId = orderDoc.getString("userId");
              String ticker = orderDoc.getString("ticker");
              String type = orderDoc.getString("type");
              
              //remove order from OrderProcessor, so we dont match it
              orderProcessorService.cancelOrder(orderId, ticker, type);
              
              //remove order fromactive orders collection
              activeOrdersCollection.deleteOne(Filters.eq("orderId", orderId));

              // if it was a buy order we credit money back to the user
              if (type.equals("BUY")) {
                double amountToCreditBack = orderDoc.getDouble("price") * orderDoc.getInteger("quantity");
                creditUser(userId, amountToCreditBack);
              }
              
              //setting cancelled where we need to 
              var orderHistoryCollection = MongoClientConnection.getCollection(
                "orderHistory"
              );
      
              if (previouslyPartiallyFilled(orderId)) {
                orderHistoryCollection.updateOne(
                  Filters.eq("orderId", orderId),
                  new Document("$set", new Document("cancelled", true))
                );
              } else {
                orderDoc.put("cancelled", true);
                orderHistoryCollection.insertOne(orderDoc);
              }
      
              ctx
              .response()
              .setStatusCode(200)
              .putHeader("Content-Type","application/json")
              .end("Order cancelled successfully");
              return;

            } else {
              ctx
                .response()
                .setStatusCode(404)
                .putHeader("Content-Type", "application/json")
                .end("Error cancelling order : " + result.errorMessage);
                return;
            }
        } catch (Exception e){
          e.printStackTrace();
          ctx.response().
          setStatusCode(500).
          putHeader("Content-Type", "application/json").
          end("Internal Server Error : Unexcpected error while cancelling order - Order could not be cancelled.");
          return;
        }
      });
  }
}
