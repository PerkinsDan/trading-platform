package orderMatchingEngine;

import java.util.Comparator;

public class SellOrderComparator implements Comparator<Order> {

  @Override
  public int compare(Order order1, Order order2) {
    // if price not equal return lower price first, most desperate to sell at the top
    if (order1.getPrice() != order2.getPrice()) {
      return Double.compare(order1.getPrice(), order2.getPrice());
    }
    return Long.compare(order1.getTimestamp(), order2.getTimestamp());
  }
}
