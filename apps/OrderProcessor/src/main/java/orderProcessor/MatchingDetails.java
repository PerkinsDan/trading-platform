package orderProcessor;

import java.util.UUID;

public class MatchingDetails {

  UUID orderID;
  double price;
  int quantityChange;
  boolean filled;

  MatchingDetails(
    UUID orderID,
    double price,
    int quantityChange,
    boolean filled
  ) {
    this.orderID = orderID;
    this.price = price;
    this.quantityChange = quantityChange;
    this.filled = filled;
  }

  public UUID getOrderID() {
    return orderID;
  }

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
