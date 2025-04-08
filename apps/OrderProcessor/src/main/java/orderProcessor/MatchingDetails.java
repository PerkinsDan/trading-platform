package orderProcessor;

import java.util.UUID;

public class MatchingDetails {

  UUID orderId;
  double price;
  int quantityChange;
  boolean filled;
  String userId;

  public MatchingDetails(
          UUID orderId,
          double price,
          int quantityChange,
          boolean filled,
          String userId
  ) {
    this.orderId = orderId;
    this.price = price;
    this.quantityChange = quantityChange;
    this.filled = filled;
    this.userId = userId;
  }

  public UUID getOrderID() {
    return orderId;
  }

  public String getUserId(){return userId;}

  public double getPrice() {
    return price;
  }

  public int getQuantityChange() {
    return quantityChange;
  }

  public boolean isFilled() {
    return filled;
  }
}
