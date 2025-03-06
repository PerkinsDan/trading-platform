package orderMatchingEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

public class OrderProcessor {

  private static OrderProcessor orderProcessor = null;
  private static final Map<Ticker, TradeBook> TradeBookMap = new HashMap<>();
  private static final Ticker[] equities = {
    Ticker.A,
    Ticker.B,
    Ticker.C,
    Ticker.D,
    Ticker.E,
  };

  private OrderProcessor() {
    for (Ticker equity : equities) {
      AddTradeBook(equity);
    }
  }

  public static OrderProcessor getInstance(){
    if(orderProcessor == null){
      orderProcessor = new OrderProcessor();
    }
    return orderProcessor;
  }

  public void addOrder(Order order) {
    TradeBook book  = OrderProcessor.getTradeBook(order.getTicker());
    book.addToBook(order);
  }

  public ArrayList<Document> MatchTrades( Order order) {
    return MatchingEngine.match(OrderProcessor.getTradeBook(order.getTicker()));
  }

  private void AddTradeBook(Ticker ticker) {
    TradeBook newTradeBook = new TradeBook(ticker);
    TradeBookMap.put(ticker, newTradeBook);
  }

  public static TradeBook getTradeBook(Ticker ticker) {
    return TradeBookMap.get(ticker);
  }
  // used only for testing
  public void resetInstance() {
    orderProcessor = null;
  }
}
