package orderProcessor;

import org.bson.Document;

import java.util.UUID;

public class Order {

  private final UUID orderId;
  private final OrderType type;
  private final double price;
  private final long timestamp;
  private final Ticker ticker;
  private int quantity;
  private boolean cancelled;
  private boolean filled;
  private final String UserId;

  public Order(OrderType type, Ticker ticker, double price, int quantity, String UserId) {
    this.orderId = UUID.randomUUID();
    this.type = type;
    this.ticker = ticker;
    this.price = price;
    this.quantity = quantity;
    this.timestamp = System.nanoTime();
    this.cancelled = false;
    this.filled = false;
    this.UserId = UserId;
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
      "Order{orderId=%s, UserId=%s, type=%s, ticker=%s, price=%.2f, quantity=%d, timestamp=%d, cancelled=%s, filled=%s}",
      orderId,
      UserId,
      type,
      ticker,
      price,
      quantity,
      timestamp,
      cancelled,
      filled
    );
  }

  public Document toDoc() {
    return new Document()
            .append("orderId", this.orderId.toString())
            .append("type", this.type.name())
            .append("ticker", this.ticker.name())
            .append("price", this.price)
            .append("quantity", this.quantity)
            .append("timestamp", this.timestamp)
            .append("cancelled", this.cancelled)
            .append("filled", this.filled);
  }
}
