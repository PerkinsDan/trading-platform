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
      timeSeries.addSnapshot(new Snapshot(ticker, price, marketOpenTime));

      rollingTimeStamp = rollingTimeStamp.plusMinutes(5);

      while (rollingTimeStamp.isBefore(marketCloseTime)) {
        // Simulate price changes using normal distribution
        price += new Random().nextGaussian() * 5;

        // Ensure price doesn't go negative
        price = Math.max(price, 0);

        timeSeries.addSnapshot(new Snapshot(ticker, price, rollingTimeStamp));
        rollingTimeStamp = rollingTimeStamp.plusMinutes(5);
      }
    }
  }
}
