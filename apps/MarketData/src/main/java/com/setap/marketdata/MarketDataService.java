package com.setap.marketdata;

import java.time.LocalTime;
import java.util.ArrayList;

public class MarketDataService {

  private final SimulatedData simulatedData;
  private static MarketDataService marketDataServiceHolder = null;

  private MarketDataService() {
    this.simulatedData = new SimulatedData();

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
      marketDataServiceHolder = new MarketDataService();
    }

    return marketDataServiceHolder;
  }

  public ArrayList<Snapshot> getTimeSeries(Tickers ticker) {
    synchronized (simulatedData) {
      return simulatedData.getTimeSeries(ticker).getSnapshots();
    }
  }

  public Snapshot getLatestSnapshot(Tickers ticker) {
    synchronized (simulatedData) {
      return simulatedData.getTimeSeries(ticker).getLatestSnapshot();
    }
  }

  public double getLatestChange(Tickers ticker) {
    synchronized (simulatedData) {
      return simulatedData.getTimeSeries(ticker).getLatestChange();
    }
  }
}
