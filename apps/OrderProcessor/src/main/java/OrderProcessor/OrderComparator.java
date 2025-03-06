package OrderProcessor;

import java.util.Comparator;

public class OrderComparator implements Comparator<Order> {

  private final OrderType type;

  public OrderComparator(OrderType type) {
    this.type = type;
  }

  @Override
  public int compare(Order order1, Order order2) {
    boolean samePrice = (order1.getPrice() == order2.getPrice());

    if (!samePrice) {
      return switch (type) {
        case BUY -> Double.compare(order2.getPrice(), order1.getPrice());
        case SELL -> Double.compare(order1.getPrice(), order2.getPrice());
      };
    }

    return Long.compare(order1.getTimestamp(), order2.getTimestamp());
  }
}
