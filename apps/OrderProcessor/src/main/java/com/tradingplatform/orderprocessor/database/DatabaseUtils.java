package com.tradingplatform.orderprocessor.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.tradingplatform.orderprocessor.orders.Order;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;

public class DatabaseUtils {

  public static boolean previouslyPartiallyFilled(String orderId) {
    MongoCollection<Document> orderHistoryCollection =
      MongoClientConnection.getCollection("orderHistory");

    Document previouslyPartiallyFilled = orderHistoryCollection
      .find(Filters.eq("orderId", orderId))
      .first();

    return previouslyPartiallyFilled != null;
  }

  public static String passValidations(JsonObject body) {
    //TODO SPLIT INTO METHODS

    MongoCollection<Document> usersCollection =
      MongoClientConnection.getCollection("users");

    String typeStr = body.getString("type");
    boolean isBuy = typeStr.equals("BUY");
    String userId = body.getString("userId");
    String tickerStr = body.getString("ticker");
    double price = body.getDouble("price");
    int quantity = body.getInteger("quantity");
    int totalPrice = (int) (price * quantity);

    Document userDoc = usersCollection
      .find(Filters.eq("userId", userId))
      .projection(Projections.include("balance"))
      .first();

    assert userDoc != null : "User document is null";
    int balance = userDoc.getInteger("balance");
    if (isBuy && balance < totalPrice) {
      System.out.println(
        "INSUFFICIENT FUNDS. User is trying to place order for " +
        totalPrice +
        ". Balance is " +
        balance
      );
      return (
        "INSUFFICIENT FUNDS. User is trying to place order for " +
        totalPrice +
        ". Balance is " +
        balance
      );
    }

    Bson stockInUsersPortfolio = Projections.elemMatch(
      "portfolio",
      Filters.eq("ticker", tickerStr)
    );

    Document doc = usersCollection
      .find(Filters.eq("userId", userId))
      .projection(stockInUsersPortfolio)
      .first();

    int numStock = 0;
    if (doc != null && doc.containsKey("portfolio")) {
      List<Document> portfolio = (List<Document>) doc.get("portfolio");
      if (!portfolio.isEmpty()) {
        numStock = portfolio.getFirst().getInteger("quantity", 0);
      }
    }

    if (!isBuy && numStock < quantity) {
      System.out.println(
        "ACCOUNT DOES NOT POSSESS SUFFICIENT STOCKS. User is trying to sell " +
        quantity +
        " " +
        tickerStr +
        " but owns " +
        numStock +
        " " +
        tickerStr
      );
      return (
        "ACCOUNT DOES NOT POSSESS SUFFICIENT STOCKS. User is trying to sell " +
        quantity +
        " " +
        tickerStr +
        " but owns " +
        numStock +
        " " +
        tickerStr
      );
    }

    return "PASSED VALIDATIONS";
  }

  public static void updateCollectionsWithMatches(
    ArrayList<String> matchesFound
  ) {
    ArrayList<Document> matchesFoundAsMongoDBDocs = convertMatchesToDocs(
      matchesFound
    );

    Document buyOrder = matchesFoundAsMongoDBDocs.get(0);
    Document sellOrder = matchesFoundAsMongoDBDocs.get(1);

    updateDbAccordingToPartiallyFilledOrNot(buyOrder, true);
    updateDbAccordingToPartiallyFilledOrNot(sellOrder, false);
  }

  private static ArrayList<Document> convertMatchesToDocs(
    ArrayList<String> matchesFound
  ) {
    ArrayList<Document> docs = new ArrayList<>();

    for (String match : matchesFound) {
      Document doc = Document.parse(match);
      docs.add(doc);
    }

    return docs;
  }

  public static void updateDbAccordingToPartiallyFilledOrNot(
    Document order,
    boolean isBuy
  ) {
    MongoCollection<Document> activeOrdersCollection =
      MongoClientConnection.getCollection("activeOrders");

    MongoCollection<Document> usersCollection =
      MongoClientConnection.getCollection("users");

    MongoCollection<Document> orderHistoryCollection =
      MongoClientConnection.getCollection("orderHistory");

    boolean filled = order.getBoolean("filled");
    String orderId = order.getString("orderID");
    String userId = order.getString("userId");
    String tickerStr = order.getString("ticker");

    int quantityChange = order.getInteger("quantityChange");
    int signedQuantityChange =
      (isBuy
          ? order.getInteger("quantityChange")
          : -order.getInteger("quantityChange"));
    double price = order.getDouble("price");
    int balanceChange =
      (isBuy
          ? -(int) (quantityChange * price)
          : (int) (quantityChange * price));

    Document partiallyFilledOrder = activeOrdersCollection
      .find(Filters.eq("orderId", orderId))
      .first();

    if (filled) {
      if (previouslyPartiallyFilled(orderId)) {
        assert partiallyFilledOrder !=
        null : "Now-filled order has been partially filled before, but is not present in activeOrdersCollection";
        partiallyFilledOrder.put("filled", true);
        orderHistoryCollection.insertOne(partiallyFilledOrder);
      } else {
        orderHistoryCollection.updateOne(
          Filters.eq("orderId", orderId),
          new Document("$set", new Document("filled", true))
        );
      }

      activeOrdersCollection.deleteOne(Filters.eq("orderId", orderId));
    } else {
      if (previouslyPartiallyFilled(orderId)) {
        assert partiallyFilledOrder !=
        null : "Partially-filled order has been partially filled before, but is not present in activeOrdersCollection";
        orderHistoryCollection.insertOne(partiallyFilledOrder);
      }

      activeOrdersCollection.updateOne(
        Filters.eq("orderId", orderId),
        new Document("$inc", new Document("quantity", signedQuantityChange))
      );
    }

    UpdateResult updateResult = usersCollection.updateOne(
      Filters.and(
        Filters.eq("userId", userId),
        Filters.elemMatch("portfolio", Filters.eq("ticker", tickerStr))
      ),
      Updates.combine(
        Updates.inc("balance", balanceChange),
        Updates.inc("portfolio.$.quantity", signedQuantityChange)
      )
    );

    //if corresponding ticker not found in user's portfolio
    if (updateResult.getMatchedCount() == 0) {
      usersCollection.updateOne(
        Filters.eq("userId", userId),
        Updates.combine(
          Updates.inc("balance", balanceChange),
          Updates.push(
            "portfolio",
            new Document("ticker", tickerStr).append(
              "quantity",
              signedQuantityChange
            )
          )
        )
      );
    }
  }
}
