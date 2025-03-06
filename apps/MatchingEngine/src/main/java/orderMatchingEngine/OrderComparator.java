package orderMatchingEngine;

import java.util.Comparator;

public class OrderComparator implements Comparator<Order> {

  private OrderType type;

  public OrderComparator(OrderType type){
    this.type = type;
  }
  @Override
  public int compare(Order order1, Order order2) {
    if (order1.getPrice() != order2.getPrice()) {
      if (type == OrderType.BUY) return Double.compare(
        order2.getPrice(),
        order1.getPrice()
      );
      return Double.compare(order1.getPrice(), order2.getPrice());
    }
    return Long.compare(order1.getTimestamp(), order2.getTimestamp());
  }
    
}
