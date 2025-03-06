package OrderProcessor;

import java.util.UUID;

public class Order {

  private final UUID orderId;
  private final OrderType type;
  private final double price;
  private final long timestamp;
  private final Ticker ticker;
  private int quantity;

  public Order(OrderType type, Ticker ticker, double price, int quantity) {
    this.orderId = UUID.randomUUID();
    this.type = type;
    this.ticker = ticker;
    this.price = price;
    this.quantity = quantity;
    this.timestamp = System.nanoTime();
  }

  public UUID getId() {
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

  public void reduceQuantity(int amount) {
    this.quantity -= amount;
  }

  @Override
  public String toString() {
    return String.format(
      "Order{orderId=%s, type=%s, ticker=%s, price=%.2f, quantity=%d, timestamp=%d}",
      orderId,
      type,
      ticker,
      price,
      quantity,
      timestamp
    );
  }
}
