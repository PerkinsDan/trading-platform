package orderProcessor;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.UUID;
import org.json.*;


public class MatchingEngine {

  private static PriorityQueue<Order> buyOrders;
  private static PriorityQueue<Order> sellOrders;
  private static ArrayList<JSONObject> matchesFound;

  public static ArrayList<JSONObject> match(TradeBook book) {
    buyOrders = book.getBuyOrders();
    sellOrders = book.getSellOrders();
    matchesFound = new ArrayList<>();

    while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
      Order buy = buyOrders.peek();
      Order sell = sellOrders.peek();

      if (buy.getPrice() < sell.getPrice()) break;

      processMatches(buy, sell);
    }

    return matchesFound;
  }

  private static void processMatches(Order buy, Order sell) {
    int quantity = Math.min(buy.getQuantity(), sell.getQuantity());
    Ticker ticker = buy.getTicker();
    double price = sell.getPrice();

    System.out.printf("Trade Executed: %d %s @ $%.2f", quantity, ticker, price);

    buy.reduceQuantity(quantity);
    sell.reduceQuantity(quantity);

    boolean sellFilled = sell.getQuantity() == 0;
    boolean buyFilled = buy.getQuantity() == 0;

    matchesFound.add(
      createChangeDocument(buy.getId(), quantity, price, buyFilled,false)
    );
    matchesFound.add(
      createChangeDocument(sell.getId(), quantity, price, sellFilled,false)
    );

    // Remove completed orders
    if (buyFilled) buyOrders.poll();
    if (sellFilled) sellOrders.poll();
  }

  private static JSONObject createChangeDocument(
    UUID id,
    int quantityChange,
    double tradePrice,
    boolean filled,
    boolean cancelled
  ) {
    JSONObject updates = new JSONObject();
      updates.put("orderId", id)
      .put("price", tradePrice)
      .put("quantityChange",quantityChange)
      .put("filled",filled);
    return updates;
  }
}