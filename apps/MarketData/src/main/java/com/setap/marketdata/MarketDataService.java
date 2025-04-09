package com.setap.marketdata;

import com.setap.marketdata.constants.Tickers;
import com.setap.marketdata.simulatedata.SimulateData;
import com.setap.marketdata.simulatedata.Snapshot;
import java.time.LocalTime;
import java.util.ArrayList;

public class MarketDataService {

  private final SimulateData simulateData;
  private static MarketDataService marketDataServiceHolder = null;

  private MarketDataService() {
    this.simulateData = new SimulateData();

    Thread dataGenerationThread = new Thread(() -> {
      boolean firstIteration = true;

      while (true) {
        if (LocalTime.now() == LocalTime.of(9, 30) || firstIteration) {
          // Regenerate the simulated data at Market Open or on the first iteration
          synchronized (simulateData) {
            simulateData.generateData();
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
    synchronized (simulateData) {
      return simulateData.getTimeSeries(ticker).getSnapshots();
    }
  }

  public Snapshot getLatestSnapshot(Tickers ticker) {
    synchronized (simulateData) {
      return simulateData.getTimeSeries(ticker).getLatestSnapshot();
    }
  }
}
