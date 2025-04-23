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

  public Boolean removeOrder(String orderId, String orderType){

    PriorityQueue<Order> targetQueue = (OrderType.valueOf(orderType) == OrderType.BUY) ? buyOrders : sellOrders;
    Order orderToRemove=null;

    for (Order order : targetQueue){
      if (order.getId().toString().equals(orderId)){
        orderToRemove = order;
        return targetQueue.remove(orderToRemove);
      }
    }

    return false;
      
  }
    
}
