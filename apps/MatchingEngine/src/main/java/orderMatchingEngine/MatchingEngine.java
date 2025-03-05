package orderMatchingEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;

import org.bson.BsonDocument;
import org.bson.Document;

public class MatchingEngine {

  private static MatchingEngine engine = null;
  private static final Map<Ticker, TradeBook> TradeBookMap =
    new HashMap<>();
  private static final Ticker[] equities = {
    Ticker.A,
    Ticker.B,
    Ticker.C,
    Ticker.D,
    Ticker.E,
  };

  public static MatchingEngine getInstance() {
    if (engine == null) {
      MatchingEngine.engine = new MatchingEngine();
      return engine;
    } else {
      return engine;
    }
  }

  private MatchingEngine() {
    for (Ticker equity : equities) {
      AddTradeBook(equity);
    }
  }

  private void AddTradeBook(Ticker ticker) {
    TradeBook newTradeBook = new TradeBook(ticker);
    TradeBookMap.put(ticker, newTradeBook);
  }

  public static TradeBook getTradeBook(Ticker ticker) {
    return TradeBookMap.get(ticker);
  }

  public ArrayList<Document> match(TradeBook book) {
    PriorityQueue<Order> buyOrders = book.getBuyBook();
    PriorityQueue<Order> sellOrders = book.getSellBook();

    ArrayList<Document> matchesFound = new ArrayList<>();

    while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
      Order buy = buyOrders.peek();
      Order sell = sellOrders.peek();

      if (buy.getPrice() < sell.getPrice()) {
        // No matches possible
        return matchesFound;
      } else {

        int quantityTraded = Math.min(buy.getQuantity(), sell.getQuantity());

        System.out.println(
          "Trade Executed: " +
          quantityTraded +
          " " +
          buy.getTicker() +
          " @ " +
          sell.getPrice()
        );

        // Reduce quantities
        buy.reduceQuantity(quantityTraded);
        sell.reduceQuantity(quantityTraded);

        Boolean removeSell  = (sell.getQuantity() == 0) ? true : false;
        Boolean removeBuy = (buy.getQuantity() == 0) ? true : false;

        //replace this with some kind of message class to notify API of whats changed
        // sell.getPrice() becuase it'll always be the lowest.
        matchesFound.add(createBSON(buy.getId(), quantityTraded, sell.getPrice(), removeBuy));
        matchesFound.add(createBSON(sell.getId(),quantityTraded,sell.getPrice(),removeSell));

        // Remove completed orders
        if (removeBuy) buyOrders.poll();
        if (removeSell) sellOrders.poll();
      }

    }
    for (Document match : matchesFound){
      System.out.println(match.toJson());
    }
    return matchesFound;
  }

  private Document createBSON(UUID id, int quantityChange, double tradePrice, Boolean filled){
    Document bsonDocument = new Document("orderId", id)
    .append("price", tradePrice)
    .append("quantityChange", quantityChange)
    .append("filled", filled);
    return bsonDocument;
  }

  // used only for testing
  public void resetInstance() {
    engine = null;
  }
}
