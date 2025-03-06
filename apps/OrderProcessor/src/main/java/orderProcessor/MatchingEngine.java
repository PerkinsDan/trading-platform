package orderProcessor;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.UUID;
import org.bson.Document;

public class MatchingEngine {

  private static PriorityQueue<Order> buyOrders;
  private static PriorityQueue<Order> sellOrders;
  private static ArrayList<Document> matchesFound;

  public static ArrayList<Document> match(TradeBook book) {
    buyOrders = book.getBuyOrders();
    sellOrders = book.getSellOrders();
    matchesFound = new ArrayList<>();

    while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
      Order buy = buyOrders.peek();
      Order sell = sellOrders.peek();

      if (buy.getPrice() < sell.getPrice()) break;

      processMatches(buy, sell);
    }

    matchesFound.forEach(match -> System.out.println(match.toJson()));

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
      createChangeDocument(buy.getId(), quantity, price, buyFilled)
    );
    matchesFound.add(
      createChangeDocument(sell.getId(), quantity, price, sellFilled)
    );

    // Remove completed orders
    if (buyFilled) buyOrders.poll();
    if (sellFilled) sellOrders.poll();
  }

  private static Document createChangeDocument(
    UUID id,
    int quantityChange,
    double tradePrice,
    boolean filled
  ) {
    return new Document("orderId", id)
      .append("price", tradePrice)
      .append("quantityChange", quantityChange)
      .append("filled", filled);
  }
}
