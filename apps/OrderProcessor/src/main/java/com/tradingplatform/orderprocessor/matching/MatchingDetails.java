package com.tradingplatform.orderprocessor.matching;

import com.tradingplatform.orderprocessor.orders.Ticker;
import java.util.UUID;

public class MatchingDetails {

  UUID orderId;
  double price;
  int quantityChange;
  boolean filled;
  String userId;
  Ticker ticker;

  public MatchingDetails(
    UUID orderId,
    double price,
    int quantityChange,
    boolean filled,
    String userId,
    Ticker ticker
  ) {
    this.orderId = orderId;
    this.price = price;
    this.quantityChange = quantityChange;
    this.filled = filled;
    this.userId = userId;
    this.ticker = ticker;
  }

  public UUID getOrderID() {
    return orderId;
  }

  public String getUserId() {
    return userId;
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

  public Ticker getTicker() {
    return ticker;
  }
}
