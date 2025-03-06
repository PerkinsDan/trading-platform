package OrderProcessor;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.UUID;


import org.bson.Document;

public class MatchingEngine {
  
  public static ArrayList<Document> match(TradeBook book) {
    PriorityQueue<Order> buyOrders = book.getBuyBook();
    PriorityQueue<Order> sellOrders = book.getSellBook();

    ArrayList<Document> matchesFound = new ArrayList<>();

    while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
      Order buy = buyOrders.peek();
      Order sell = sellOrders.peek();

      if (buy.getPrice() < sell.getPrice()) {
        // No matches possible
        return matchesFound;
      }
      int quantityTraded = Math.min(buy.getQuantity(), sell.getQuantity());

      System.out.println(
        "Trade Executed: " +
        quantityTraded +
        " " +
        buy.getTicker() +
        " @ " +
        sell.getPrice()
      );

      buy.reduceQuantity(quantityTraded);
      sell.reduceQuantity(quantityTraded);

      Boolean removeSell  = (sell.getQuantity() == 0) ? true : false;
      Boolean removeBuy = (buy.getQuantity() == 0) ? true : false;

      matchesFound.add(createBSON(buy.getId(), quantityTraded, sell.getPrice(), removeBuy));
      matchesFound.add(createBSON(sell.getId(),quantityTraded,sell.getPrice(),removeSell));

      // Remove completed orders
      if (removeBuy) buyOrders.poll();
      if (removeSell) sellOrders.poll();
    }

    for (Document match : matchesFound){
      System.out.println(match.toJson());
    }
    return matchesFound;
  }

  private static Document createBSON(UUID id, int quantityChange, double tradePrice, Boolean filled){
    Document bsonDocument = new Document("orderId", id)
    .append("price", tradePrice)
    .append("quantityChange", quantityChange)
    .append("filled", filled);
    return bsonDocument;
  }
}

