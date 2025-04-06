package com.setap.marketdata;

import java.time.LocalTime;
import java.util.ArrayList;

public class MarketDataService {

  private final SimulatedData simulatedData;
  private static MarketDataService marketDataServiceHolder = null;

  private MarketDataService(ArrayList<String> tickers) {
    this.simulatedData = new SimulatedData(tickers);

    Thread dataGenerationThread = new Thread(() -> {
      boolean firstIteration = true;

      while (true) {
        if (LocalTime.now() == LocalTime.of(9, 30) || firstIteration) {
          // Regenerate the simulated data at Market Open or on the first iteration
          synchronized (simulatedData) {
            simulatedData.generateData();
            firstIteration = false;
          }
        }

        try {
          Thread.sleep(60 * 1000); // Sleep for 1 minute
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    });

    dataGenerationThread.setDaemon(true);
    dataGenerationThread.start();
  }

  public static MarketDataService getInstance() {
    if (marketDataServiceHolder == null) {
      throw new IllegalStateException(
        "MarketDataService not initialized. Call createInstance() first."
      );
    }

    return marketDataServiceHolder;
  }

  public static MarketDataService createInstance(ArrayList<String> tickers) {
    if (marketDataServiceHolder != null) {
      return marketDataServiceHolder;
    }

    marketDataServiceHolder = new MarketDataService(tickers);
    return marketDataServiceHolder;
  }

  public ArrayList<Snapshot> getTimeSeries(String ticker) {
    synchronized (simulatedData) {
      return simulatedData.getTimeSeries(ticker).getSnapshots();
    }
  }

  public Snapshot getLatestSnapshot(String ticker) {
    synchronized (simulatedData) {
      return simulatedData.getTimeSeries(ticker).getLatestSnapshot();
    }
  }

  public double getLatestChange(String ticker) {
    synchronized (simulatedData) {
      return simulatedData.getTimeSeries(ticker).getLatestChange();
    }
  }
}
