package orderProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;

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

  public Boolean cancelOrder(String orderId, String orderTicker, String orderType) {
    
    try {
      TradeBook book = orderProcessor.getTradeBook(Ticker.valueOf(orderTicker));
      PriorityQueue<Order> orderQueue;

      if (OrderType.valueOf(orderType) == OrderType.BUY) {
        orderQueue = book.getBuyOrders();
      } else {
        orderQueue = book.getSellOrders();
      }
      
      return orderQueue.remove(order);
    } catch (Exception e) {
      // TODO: handle exception
    }
    
  }

  private Ticker getTickerFromString()

  public void resetInstance() {
    orderProcessor = null;
  }
}
