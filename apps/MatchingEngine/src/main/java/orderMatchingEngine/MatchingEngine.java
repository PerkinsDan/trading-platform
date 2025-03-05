package orderMatchingEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

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

  public int match(TradeBook book) {
    PriorityQueue<Order> buyOrders = book.getBuyBook();
    PriorityQueue<Order> sellOrders = book.getSellBook();

    int matchesFound = 0;

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

        //replace this with some kind of message class to notify API of whats changed
        matchesFound++;

        // Reduce quantities
        buy.reduceQuantity(quantityTraded);
        sell.reduceQuantity(quantityTraded);

        // Remove completed orders
        if (buy.getQuantity() == 0) buyOrders.poll();
        if (sell.getQuantity() == 0) sellOrders.poll();
      }
    }
    return matchesFound;
  }

  // used only for testing
  public void resetInstance() {
    engine = null;
  }
}
