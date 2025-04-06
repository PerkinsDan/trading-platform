package com.setap.marketdata;

import java.time.LocalTime;
import java.util.ArrayList;

public class MarketDataService {

  private final SimulatedData simulatedData;

  public MarketDataService(ArrayList<String> tickers) {
    this.simulatedData = new SimulatedData(tickers);

    Thread dataGenerationThread = new Thread(() -> {
      boolean firstIteration = true;

      while (true) {
        if (LocalTime.now() == LocalTime.MIDNIGHT || firstIteration) {
          // Reset the simulated data at midnight
          synchronized (simulatedData) {
            simulatedData.generateData();
            firstIteration = false;
          }
        }

        try {
          Thread.sleep(5 * 60 * 1000); // Sleep for 5 minutes
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    });

    dataGenerationThread.setDaemon(true);
    dataGenerationThread.start();
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
