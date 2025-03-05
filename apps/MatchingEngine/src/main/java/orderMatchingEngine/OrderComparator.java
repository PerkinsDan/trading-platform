package orderMatchingEngine;

import java.util.Comparator;

public class OrderComparator implements Comparator<Order> {

  Order.Type type;

  public OrderComparator(Order.Type type) {
    this.type = type;
  }

  @Override
  public int compare(Order order1, Order order2) {
    // if price not equal return higher price first, most desperate to buy at the top
    if (order1.getPrice() != order2.getPrice()) {
      if (type == Order.Type.BUY) return Double.compare(
        order2.getPrice(),
        order1.getPrice()
      );
      return Double.compare(order1.getPrice(), order2.getPrice());
    }
    // if the price is equal then return lower timestamp first because that order was submitted first
    return Long.compare(order1.getTimestamp(), order2.getTimestamp());
  }
}
