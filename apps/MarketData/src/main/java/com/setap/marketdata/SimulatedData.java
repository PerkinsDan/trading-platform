package com.setap.marketdata;

import java.time.LocalTime;
import java.util.*;

public class SimulatedData {

  private final LocalTime marketOpenTime = LocalTime.of(9, 30, 0);
  private final LocalTime marketCloseTime = LocalTime.of(16, 0, 0);

  private final Map<Tickers, TimeSeries> timeSeriesMap = new HashMap<>();

  public SimulatedData() {
    for (Tickers ticker : Tickers.values()) {
      timeSeriesMap.put(ticker, new TimeSeries());
    }
  }

  public TimeSeries getTimeSeries(Tickers ticker) {
    return timeSeriesMap.get(ticker);
  }

  public void generateData() {
    System.out.println("Generating simulated data...");

    for (Tickers ticker : timeSeriesMap.keySet()) {
      TimeSeries timeSeries = timeSeriesMap.get(ticker);
      LocalTime rollingTimeStamp = marketOpenTime;

      // Simulated starting price
      double price = Math.random() * 1000;
      price = Math.round(price * 100.0) / 100.0;

      timeSeries.addSnapshot(new Snapshot(price, marketOpenTime, 0));

      rollingTimeStamp = rollingTimeStamp.plusMinutes(5);

      while (rollingTimeStamp.isBefore(marketCloseTime)) {
        double previousPrice = price;

        // Simulate price changes using normal distribution
        price += new Random().nextGaussian() * 5;
        price = Math.round(price * 100.0) / 100.0;

        // Ensure price doesn't go negative
        price = Math.max(price, 0);

        double change = ((price - previousPrice) / previousPrice) * 100;
        change = Math.round(change * 100.0) / 100.0;

        timeSeries.addSnapshot(new Snapshot(price, rollingTimeStamp, change));
        rollingTimeStamp = rollingTimeStamp.plusMinutes(5);
      }
    }
  }
}
