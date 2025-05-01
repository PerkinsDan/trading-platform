package com.tradingplatform.orderprocessor.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.tradingplatform.orderprocessor.orders.Order;
import com.tradingplatform.orderprocessor.orders.Ticker;
import com.tradingplatform.orderprocessor.validations.ValidationResult;

import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;

public class DatabaseUtils {

  public static void creditUser(String userId, double amountToAdd){
    
    var usersCollection = MongoClientConnection.getCollection("users"); 
    
    usersCollection.updateOne(
      Filters.eq("userId", userId),
      new Document("$inc", new Document("balance", amountToAdd))
    );
  }

  public static boolean previouslyPartiallyFilled(String orderId) {
    MongoCollection<Document> orderHistoryCollection =
      MongoClientConnection.getCollection("orderHistory");

    Document previouslyPartiallyFilled = orderHistoryCollection
      .find(Filters.eq("orderId", orderId))
      .first();

    return previouslyPartiallyFilled != null;
  }
  

  public static ValidationResult userBalanceIsSufficientForBuy(JsonObject body){
    MongoCollection<Document> usersCollection =
    MongoClientConnection.getCollection("users");

    boolean isBuy = body.getString("type").equals("BUY");
    double totalPrice = (body.getDouble("price") * body.getInteger("quantity"));

    Document userDoc = usersCollection
      .find(Filters.eq("userId", body.getString("userId")))
      .projection(Projections.include("balance"))
      .first();

    int balance = userDoc.getInteger("balance");

    if (isBuy) {
      if (balance < totalPrice){
        return ValidationResult.fail("Insufficient funds to place thi order");
      } else {
        ValidationResult.ok();
      }
    } else {
      return ValidationResult.fail("Order Type is not a BUY");
    }
    return ValidationResult.fail("Unexpected error while validating user balance for buy order.");
  }

  public static ValidationResult userPortfolioIsSufficientForSell(JsonObject body){

    boolean isSell = body.getString("type").equals("SELL");

    MongoCollection<Document> usersCollection =
    MongoClientConnection.getCollection("users");

    Bson stockInUsersPortfolio = Projections.elemMatch(
      "portfolio",
      Filters.eq("ticker", body.getString("ticker"))
    );

    Document doc = usersCollection
    .find(Filters.eq("userId", body.getString("userId")))
    .projection(stockInUsersPortfolio)
    .first();

    int numStock = 0;
    if (doc != null && doc.containsKey("portfolio")) {
      List<Document> portfolio = (List<Document>) doc.get("portfolio");
      if (!portfolio.isEmpty()) {
        numStock = portfolio.getFirst().getInteger("quantity", 0);
      }
    }
    if (isSell) {
      if (numStock < body.getInteger("quantity")){
        return ValidationResult.fail("Insufficient quantity of stock owned to place this order");
      } else {
        ValidationResult.ok();
      }
    } else {
      return ValidationResult.fail(" Error validating quantity owned is sufficient : Order Type is not a SELL");
    }
    return ValidationResult.fail("Unexpected error while validating users portfolio has sufficient stock to complete sell order.");
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
    int signedQuantityChange = (isBuy ? quantityChange : -quantityChange);
    double price = order.getDouble("price");
    double balanceChange =
      (isBuy ? -(quantityChange * price) : (quantityChange * price));

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
