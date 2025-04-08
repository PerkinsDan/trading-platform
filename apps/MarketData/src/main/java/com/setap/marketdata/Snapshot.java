package com.setap.marketdata;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

public class Snapshot {

  private final double price;
  private final LocalTime timestamp;
  private final double change;

  public Snapshot(double price, LocalTime timestamp, double change) {
    this.price = price;
    this.timestamp = timestamp;
    this.change = change;
  }

  public double getPrice() {
    return price;
  }

  public LocalTime getTimestamp() {
    return timestamp;
  }

  public double getChange() {
    return change;
  }
}
