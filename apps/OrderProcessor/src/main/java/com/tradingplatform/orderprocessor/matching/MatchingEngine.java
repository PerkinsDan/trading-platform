package com.tradingplatform.orderprocessor.matching;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingplatform.orderprocessor.orders.Order;
import com.tradingplatform.orderprocessor.orders.Ticker;
import com.tradingplatform.orderprocessor.orders.TradeBook;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class MatchingEngine {

  private static PriorityQueue<Order> buyOrders;
  private static PriorityQueue<Order> sellOrders;
  private static ArrayList<String> matchesFound;

  public static ArrayList<String> match(TradeBook book) {
    buyOrders = book.getBuyOrders();
    sellOrders = book.getSellOrders();

    matchesFound = new ArrayList<>();

    while (matchPossible(buyOrders, sellOrders)) {
      processMatches(buyOrders.peek(), sellOrders.peek());
    }

    return matchesFound;
  }

  private static void processMatches(Order buy, Order sell) {
    int quantity = Math.min(buy.getQuantity(), sell.getQuantity());
    Ticker ticker = buy.getTicker();
    double price = sell.getPrice();

    System.out.printf(
      "Trade Executed: %d %s @ $%.2f%n",
      quantity,
      ticker,
      price
    );

    buy.reduceQuantity(quantity);
    sell.reduceQuantity(quantity);

    boolean sellFilled = sell.getQuantity() == 0;
    boolean buyFilled = buy.getQuantity() == 0;

    ObjectMapper mapper = new ObjectMapper();
    try {
      matchesFound.add(
        mapper.writeValueAsString(
          new MatchingDetails(
            buy.getId(),
            price,
            quantity,
            buyFilled,
            buy.getUserId(),
            buy.getTicker()
          )
        )
      );
      matchesFound.add(
        mapper.writeValueAsString(
          new MatchingDetails(
            sell.getId(),
            price,
            quantity,
            sellFilled,
            sell.getUserId(),
            sell.getTicker()
          )
        )
      );
    } catch (JsonProcessingException e) {
      System.err.printf(
        "Exception processing matches to JSON %s",
        e.getMessage()
      );
    }

    // Remove completed orders
    if (buyFilled) buyOrders.poll();
    if (sellFilled) sellOrders.poll();
  }

  private static boolean matchPossible(
    PriorityQueue<Order> buyOrders,
    PriorityQueue<Order> sellOrders
  ) {
    Order buy = buyOrders.peek();
    Order sell = sellOrders.peek();
    return (
      !buyOrders.isEmpty() &&
      !sellOrders.isEmpty() &&
      buy.getPrice() >= sell.getPrice()
    );
  }
}
