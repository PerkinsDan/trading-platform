package com.setap.marketdata;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

  /**
   * Generates simulated market data for each ticker.
   * This method simulates the generation of market data for a single day.
   */
  public void generateData() {
    System.out.println("Generating simulated data...");

    for (Tickers ticker : Tickers.values()) {
      TimeSeries timeSeries = timeSeriesMap.get(ticker);
      LocalTime rollingTimeStamp = marketOpenTime;

      // Simulated starting price
      double price = Math.random() * 1000;
      timeSeries.addSnapshot(
        new Snapshot(ticker.toString(), price, marketOpenTime)
      );

      rollingTimeStamp = rollingTimeStamp.plusMinutes(5);

      while (rollingTimeStamp.isBefore(marketCloseTime)) {
        // Simulate price changes using normal distribution
        price += new Random().nextGaussian() * 5;

        // Ensure price doesn't go negative
        price = Math.max(price, 0);

        timeSeries.addSnapshot(
          new Snapshot(ticker.toString(), price, rollingTimeStamp)
        );
        rollingTimeStamp = rollingTimeStamp.plusMinutes(5);
      }
    }
  }
}
