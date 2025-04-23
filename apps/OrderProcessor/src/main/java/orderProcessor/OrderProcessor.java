package orderProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    
      TradeBook book = orderProcessor.getTradeBook(Ticker.valueOf(orderTicker));
      return book.removeOrder(orderId, orderType);
  }

  public void resetInstance() {
    orderProcessor = null;
  }
}
