package orderProcessor;

import java.util.PriorityQueue;

public class TradeBook {

  private final PriorityQueue<Order> buyOrders = new PriorityQueue<>(
    new OrderComparator(OrderType.BUY)
  );
  private final PriorityQueue<Order> sellOrders = new PriorityQueue<>(
    new OrderComparator(OrderType.SELL)
  );

  public void addToBook(Order order) {
    switch (order.getType()) {
      case BUY -> buyOrders.offer(order);
      case SELL -> sellOrders.offer(order);
    }
  }

  public PriorityQueue<Order> getBuyOrders() {
    return buyOrders;
  }

  public PriorityQueue<Order> getSellOrders() {
    return sellOrders;
  }
}
