package orderProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class OrderProcessor {

  private static OrderProcessor orderProcessor = null;
  private static final Map<Ticker, TradeBook> tradeBookMap = new HashMap<>();

  private OrderProcessor() {
    for (Ticker ticker : Ticker.values()) {
      tradeBookMap.put(ticker, new TradeBook());
    }
  }

  public static OrderProcessor getInstance() {
    if (orderProcessor == null) {
      orderProcessor = new OrderProcessor();
    }
    return orderProcessor;
  }

  public ArrayList<String> processOrder(Order order) {
    TradeBook book = orderProcessor.getTradeBook(order.getTicker());
    book.addToBook(order);

    return MatchingEngine.match(orderProcessor.getTradeBook(order.getTicker()));
  }

  public TradeBook getTradeBook(Ticker ticker) {
    return tradeBookMap.get(ticker);
  }

  public Boolean cancelOrder(Order order) {
    TradeBook book = orderProcessor.getTradeBook(order.getTicker());
    PriorityQueue<Order> orderQueue;

    if (order.getType() == OrderType.BUY) {
      orderQueue = book.getBuyOrders();
    } else {
      orderQueue = book.getSellOrders();
    }

    return orderQueue.remove(order);
  }

  public void resetInstance() {
    orderProcessor = null;
  }
}
