package com.tradingplatform.orderprocessor.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
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

    double balance = userDoc.getDouble("balance");

    if (isBuy) {
      if (balance < totalPrice){
        return ValidationResult.fail("Insufficient funds to place this order");
      } else {
        return ValidationResult.ok();
      }
    } else {
      return ValidationResult.fail("Order Type is not a BUY");
    }
  }

  public static ValidationResult userPortfolioIsSufficientForSell(JsonObject body){

    boolean isSell = body.getString("type").equals("SELL");
    
    if(!isSell){
      return ValidationResult.fail(" Error validating quantity owned is sufficient : Order Type is not a SELL");
    }
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
    
    if (numStock < body.getInteger("quantity")){
      return ValidationResult.fail("Insufficient quantity of stock owned to place this order");
    }
      
    return ValidationResult.ok();
    
  
  }

  public static void updateCollectionsWithMatches(
    ArrayList<String> matchesFound
  ) {
    ArrayList<Document> matchesAsDocs = convertMatchesToDocs(matchesFound);
    MongoCollection<Document> activeOrders =
      MongoClientConnection.getCollection("activeOrders");
    MongoCollection<Document> orderHistory =
      MongoClientConnection.getCollection("orderHistory");
    MongoCollection<Document> users = MongoClientConnection.getCollection(
      "users"
    );
    boolean isBuy = true;

    for (Document match : matchesAsDocs) {
      Document orderDoc = getDocByOrderId(
        match.getString("orderID"),
        activeOrders
      );
      double netBalanceChange =
        match.getDouble("price") * match.getInteger("quantityChange");

      if (isBuy) {
        int signedQuantityChange = match.getInteger("quantityChange");

        updatePortfolio(match, users, signedQuantityChange);

        if (match.getBoolean("filled")) {
          moveToHistory(orderDoc, orderHistory, activeOrders);
        }
      } else {
        int signedQuantityChange = -match.getInteger("quantityChange");

        updatePortfolio(match, users, signedQuantityChange);
        updateBalance(match.getString("userId"), users, netBalanceChange);

        if (match.getBoolean("filled")) {
          moveToHistory(orderDoc, orderHistory, activeOrders);
        }
      }

      isBuy = !isBuy;
    }
  }

  private static Document getDocByOrderId(
    String orderId,
    MongoCollection<Document> collection
  ) {
    return collection.find(Filters.eq("orderId", orderId)).first();
  }

  private static void updatePortfolio(
    Document match,
    MongoCollection<Document> usersCollection,
    int signedQuantityChange
  ) {
    String userId = match.getString("userId");
    String ticker = match.getString("ticker");
    UpdateResult updateResult = usersCollection.updateOne(
      Filters.and(
        Filters.eq("userId", userId),
        Filters.elemMatch("portfolio", Filters.eq("ticker", ticker))
      ),
      Updates.inc("portfolio.$.quantity", signedQuantityChange)
    );

    if (updateResult.getMatchedCount() == 0) {
      usersCollection.updateOne(
        Filters.eq("userId", userId),
        Updates.push(
          "portfolio",
          new Document("ticker", ticker).append(
            "quantity",
            signedQuantityChange
          )
        )
      );
    }
  }

  private static void updateBalance(
    String userId,
    MongoCollection<Document> collection,
    double signedBalanceChange
  ) {
    collection.updateOne(
      Filters.eq("userId", userId),
      new Document("$inc", new Document("balance", signedBalanceChange))
    );
  }

  private static void moveToHistory(
    Document Order,
    MongoCollection<Document> orderHistory,
    MongoCollection<Document> activeOrders
  ) {
    activeOrders.deleteOne(Filters.eq("orderId", Order.getString("orderID")));
    Order.put("filled", true);
    orderHistory.insertOne(Order);
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
}
