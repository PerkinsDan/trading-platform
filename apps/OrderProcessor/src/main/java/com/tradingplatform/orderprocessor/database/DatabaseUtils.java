package com.tradingplatform.orderprocessor.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
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

    System.out.println(body);

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

    double balance = userDoc.getDouble("balance");
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

  public static void updateCollectionsWithMatches(ArrayList<String> matchesFound){

    ArrayList<Document> matchesAsDocs = convertMatchesToDocs(matchesFound);
    MongoCollection<Document> activeOrders = MongoClientConnection.getCollection("activeOrders");
    MongoCollection<Document> orderHistory = MongoClientConnection.getCollection("orderHistory");
    MongoCollection<Document> users = MongoClientConnection.getCollection("users");
    Boolean isBuy = true;

    for( Document match : matchesAsDocs){
      
      Document orderDoc = getDocByOrderId(match.getString("orderId"), activeOrders);
      double netBalanceChange = match.getDouble("price") * match.getInteger("quantity");
      
      if(isBuy){
        double signedBalanceChange = -netBalanceChange;
        int signedQuantityChange = match.getInteger("quantityChange");
        
        updatePortfolio(match, users, signedQuantityChange);
        updateBalance(match.getString("userId"), users, signedBalanceChange);

        if(match.getBoolean("filled")){
          moveToHistory(orderDoc,activeOrders, orderHistory);
        }

      } else {
        // its the sell side 
        double signedBalanceChange = netBalanceChange;
        int signedQuantityChange = -match.getInteger("quantityChange");

        updatePortfolio(match, users, signedQuantityChange);
        updateBalance(match.getString("userId"), users, signedBalanceChange);
        
        if(match.getBoolean("filled")){
          moveToHistory(orderDoc, orderHistory, activeOrders);
        } 
      }

      isBuy = !isBuy;

    }

  }
  
  private static Document getDocByOrderId(String orderId, MongoCollection<Document> collection){
    return collection.find(Filters.eq("orderId", orderId)).first();
  }

  private static void updatePortfolio(Document match, MongoCollection<Document> usersCollection, double signedQuantityChange){
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
          new Document("ticker", ticker)
              .append("quantity", signedQuantityChange)
        )
      );
    }
    
  }

  private static void updateBalance(String userId, MongoCollection<Document> collection, double signedBalanceChange){
    collection.updateOne(
      Filters.eq("userId", userId),
      new Document("$inc", new Document("quantity",signedBalanceChange))
    );
  }

  private static void moveToHistory(Document Order, MongoCollection<Document> orderHistory, MongoCollection<Document> activeOrders){
    activeOrders.deleteOne(Filters.eq("orderId", Order.getString("orderId")));
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
