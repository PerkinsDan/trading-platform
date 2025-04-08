package com.setap.marketdata;

import java.time.LocalTime;

public class Snapshot {

  private final Tickers ticker;
  private final double price;
  private final LocalTime timestamp;

  public Snapshot(Tickers ticker, double price, LocalTime timestamp) {
    this.ticker = ticker;
    this.price = price;
    this.timestamp = timestamp;
  }

  public double getPrice() {
    return price;
  }

  public LocalTime getTimestamp() {
    return timestamp;
  }

  @Override
  public String toString() {
    return (
      "Snapshot{" +
      "ticker='" +
      ticker +
      '\'' +
      ", price=" +
      price +
      ", timestamp=" +
      timestamp +
      '}'
    );
  }
}
