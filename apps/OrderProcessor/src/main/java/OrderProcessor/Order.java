package OrderProcessor;

import java.util.UUID;
public class Order {

  private final UUID orderId;
  private final OrderType type;
  private final double price;
  private long timestamp;
  private final Ticker ticker;
  private int quantity; // quantity isnt final becuase it can change during partial fills
  private Boolean filled;

  public Order(OrderType type, Ticker ticker, double price, int quantity) {
    this.orderId = UUID.randomUUID();
    this.type = type;
    this.ticker = ticker;
    this.price = price;
    this.quantity = quantity;
    this.timestamp = System.nanoTime(); // FIFO tie-breaker
    this.filled = false;
  }

  public UUID getId(){
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

  public void setTimeStamp(long timestamp){
    this.timestamp = timestamp;
  }

  public Boolean getFilled(){
    return filled;
  }

  public void setFilledTrue(){
    filled = true;
  }

  @Override
  public String toString() {
    return "Order{" +
            "orderId=" + orderId +
            ", type=" + type +
            ", ticker=" + ticker +
            ", price=" + price +
            ", quantity=" + quantity +
            ", timestamp=" + timestamp +
            ", filled=" + filled +
            '}';
  }
}
