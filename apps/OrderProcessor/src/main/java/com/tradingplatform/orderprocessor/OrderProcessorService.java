package com.tradingplatform.orderprocessor;

import com.tradingplatform.orderprocessor.matching.MatchingEngine;
import com.tradingplatform.orderprocessor.orders.Order;
import com.tradingplatform.orderprocessor.orders.Ticker;
import com.tradingplatform.orderprocessor.orders.TradeBook;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderProcessorService {

  private static OrderProcessorService orderProcessor = null;
  private static final Map<Ticker, TradeBook> tradeBookMap = new HashMap<>();

  private OrderProcessorService() {
    for (Ticker ticker : Ticker.values()) {
      tradeBookMap.put(ticker, new TradeBook());
    }
  }

  public static OrderProcessorService getInstance() {
    if (orderProcessor == null) {
      orderProcessor = new OrderProcessorService();
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
