package OrderProcessor;

import java.util.PriorityQueue;

public class TradeBook {

  private final Ticker ticker;
  private final PriorityQueue<Order> buySide;
  private final PriorityQueue<Order> sellSide;

  public TradeBook(Ticker ticker) {
    this.buySide = new PriorityQueue<>(new OrderComparator(OrderType.BUY));
    this.sellSide = new PriorityQueue<>(new OrderComparator(OrderType.SELL));
    this.ticker = ticker;
  }

  public void addToBook(Order order) {
    switch (order.getType()) {
      case OrderType.BUY:
        buySide.offer(order);
        break;
      case OrderType.SELL:
        sellSide.offer(order);
        break;
    }
  }

  public PriorityQueue<Order> getBuyBook() {
    return buySide;
  }

  public PriorityQueue<Order> getSellBook() {
    return sellSide;
  }

  public String getTicker() {
    return ticker.name();
  }
}
