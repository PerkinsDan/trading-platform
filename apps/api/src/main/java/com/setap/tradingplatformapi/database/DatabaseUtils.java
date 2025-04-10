package com.setap.tradingplatformapi.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import orderProcessor.Order;
import orderProcessor.OrderProcessor;
import orderProcessor.OrderType;
import orderProcessor.Ticker;
import org.bson.Document;

public class DatabaseUtils {

  public DatabaseUtils() {}

  public static Order createOrderAndInsertIntoDatabase(
    JsonObject body,
    MongoCollection<Document> activeOrdersCollection
  ) {
    String typeStr = body.getString("type");
    String tickerStr = body.getString("ticker");
    double price = body.getDouble("price");
    int quantity = body.getInteger("quantity");
    String userId = body.getString("userId");

    OrderType type = OrderType.valueOf(typeStr);
    Ticker ticker = Ticker.valueOf(tickerStr);

    Order order = new Order(type, userId, ticker, price, quantity);
    Document orderDoc = order.toDoc();

    activeOrdersCollection.insertOne(orderDoc);

    return order;
  }

  public static ArrayList<Document> processOrderAndParseMatchesFound(
    Order order,
    OrderProcessor orderprocessor
  ) {
    ArrayList<String> matchesFound = orderprocessor.processOrder(order);
    ArrayList<Document> matchesFoundAsMongoDBDocs = new ArrayList<>();
    for (String match : matchesFound) {
      Document doc = Document.parse(match);
      matchesFoundAsMongoDBDocs.add(doc);
    }
    return matchesFoundAsMongoDBDocs;
  }

  public static void updateDb(
    ArrayList<Document> matchesFoundAsMongoDBDocs,
    MongoCollection<Document> activeOrdersCollection,
    MongoCollection<Document> usersCollection,
    MongoCollection<Document> orderHistoryCollection
  ) {
    Document buyOrder = matchesFoundAsMongoDBDocs.get(0);
    Document sellOrder = matchesFoundAsMongoDBDocs.get(1);

    updateDbAccordingToPartiallyFilledOrNot(
      buyOrder,
      true,
      activeOrdersCollection,
      usersCollection,
      orderHistoryCollection
    );
    updateDbAccordingToPartiallyFilledOrNot(
      sellOrder,
      false,
      activeOrdersCollection,
      usersCollection,
      orderHistoryCollection
    );
  }

  public static void updateDbAccordingToPartiallyFilledOrNot(
    Document order,
    boolean isBuy,
    MongoCollection<Document> activeOrdersCollection,
    MongoCollection<Document> usersCollection,
    MongoCollection<Document> orderHistoryCollection
  ) {
    boolean filled = order.getBoolean("filled");
    String orderId = order.getString("orderID");
    String userId = order.getString("userId");

    int quantityChange = order.getInteger("quantityChange");
    double price = order.getDouble("price");
    int amountChange = (int) (quantityChange * price);
    int balanceChange = (isBuy ? -amountChange : amountChange);

    Document previouslyPartiallyFilled = orderHistoryCollection
      .find(Filters.eq("orderId", orderId))
      .first();

        Document partiallyFilledOrder = activeOrdersCollection
                .find(Filters.eq("orderId", orderId))
                .first();

        if (filled) {
            if (previouslyPartiallyFilled == null) {
                partiallyFilledOrder.put("filled", true);
                orderHistoryCollection.insertOne(partiallyFilledOrder);
            } else {
                orderHistoryCollection.updateOne(
                        Filters.eq("orderId", orderId),
                        new Document("$set", new Document("filled", true))
                );
            }

            activeOrdersCollection.deleteOne(
                    Filters.eq("orderId", orderId)
            );

        } else {
            if (previouslyPartiallyFilled == null) {
                orderHistoryCollection.insertOne(partiallyFilledOrder);
            }

      activeOrdersCollection.updateOne(
        Filters.eq("orderId", orderId),
        new Document("$inc", new Document("quantity", -quantityChange))
      );
    }
    usersCollection.updateOne(
      Filters.eq("userId", userId),
      new Document("$inc", new Document("balance", balanceChange))
    );
  }
}
