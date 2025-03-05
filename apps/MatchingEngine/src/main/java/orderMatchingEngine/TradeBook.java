package orderMatchingEngine;

import java.util.PriorityQueue;

public class TradeBook {

  private final Ticker ticker;
  private final PriorityQueue<Order> buyBook;
  private final PriorityQueue<Order> sellBook;

  public TradeBook(Ticker ticker) {
    this.buyBook = new PriorityQueue<>(new BuyOrderComparator());
    this.sellBook = new PriorityQueue<>(new SellOrderComparator());
    this.ticker = ticker;
  }

  public void addToBook(Order order) {
    switch (order.getType()) {
      case OrderType.BUY:
        buyBook.offer(order);
        break;
      case OrderType.SELL:
        sellBook.offer(order);
        break;
    }
  }

  public PriorityQueue<Order> getBuyBook() {
    return buyBook;
  }

  public PriorityQueue<Order> getSellBook() {
    return sellBook;
  }

  public String getTicker() {
    return ticker.name();
  }
}
