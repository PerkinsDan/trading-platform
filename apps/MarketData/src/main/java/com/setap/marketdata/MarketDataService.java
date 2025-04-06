package com.setap.marketdata;

import java.util.ArrayList;

public class MarketDataService {

  private final SimulatedData simulatedData;

  public MarketDataService() {
    this.simulatedData = new SimulatedData();

    Thread dataGenerationThread = new Thread(() -> {
      while (true) {
        synchronized (simulatedData) {
          simulatedData.generateData();
        }

        try {
          // Sleep for 24 hours before generating data again
          Thread.sleep(24 * 60 * 60 * 1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    });

    dataGenerationThread.setDaemon(true);
    dataGenerationThread.start();
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
