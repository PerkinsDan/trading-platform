package orderMatchingEngine;

import java.util.concurrent.atomic.AtomicLong;

public class Order {

  // automatic UUID generator for setting the order type
  private static final AtomicLong counter = new AtomicLong(0);

  private final long orderId;
  //private final long orderId : implement later, dont know enough about it currently
  private final OrderType type;
  private final double price;
  private final long timestamp;
  private final Ticker ticker;
  private int quantity; // quantity isnt final becuase it can change during partial fills

  public Order(OrderType type, Ticker ticker, double price, int quantity) {
    this.orderId = counter.getAndIncrement();
    this.type = type;
    this.ticker = ticker;
    this.price = price;
    this.quantity = quantity;
    this.timestamp = System.nanoTime(); // FIFO tie-breaker
  }

  // secondary constructor to force equal timestamps, used for testing
  public Order(
    OrderType type,
    Ticker ticker,
    double price,
    int quantity,
    long timestamp
  ) {
    this.orderId = counter.getAndIncrement();
    this.type = type;
    this.ticker = ticker;
    this.price = price;
    this.quantity = quantity;
    this.timestamp = timestamp;
  }

  public long getId() {
    return orderId;
  }

  public OrderType getType() {
    return type;
  }

  public double getPrice() {
    return price;
  }

  public int getQuantity() {
    return quantity;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public Ticker getTicker() {
    return ticker;
  }

  public void reduceQuantity(int decrement) {
    this.quantity -= decrement;
  }

  @Override
  public String toString() {
    return String.format(
      "Order{id=%d, type=%s, ticker=%s, price=%.2f, quantity=%d, timestamp=%d}",
      orderId,
      type,
      ticker,
      price,
      quantity,
      timestamp
    );
  }
}
