package com.tradingplatform.marketdata;

import com.tradingplatform.marketdata.constants.Ticker;
import com.tradingplatform.marketdata.simulatedata.SimulateData;
import com.tradingplatform.marketdata.simulatedata.Snapshot;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

public class MarketDataService {

  private final SimulateData simulateData;
  private static MarketDataService marketDataServiceHolder = null;

  private final int ONE_HOUR = 60 * 60 * 1000;
  private final LocalTime PRE_MARKET_OPEN = LocalTime.of(9, 0);

  private MarketDataService() {
    this.simulateData = new SimulateData();
    Thread dataGenerationThread = getDataGenerationThread();
    dataGenerationThread.setDaemon(true);
    dataGenerationThread.start();
  }

  private Thread getDataGenerationThread() {
    return new Thread(() -> {
      boolean firstIteration = true;

      long timeDifference = Math.abs(
        Duration.between(LocalTime.now(), PRE_MARKET_OPEN).toMinutes()
      );

      while (true) {
        try {
          if (timeDifference <= 30 || firstIteration) {
            synchronized (simulateData) {
              simulateData.generateData();
              firstIteration = false;
            }
          }
          Thread.sleep(ONE_HOUR);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        } catch (Exception e) {
          System.out.println("Error during data generation: " + e);
        }
      }
    });
  }

  public static MarketDataService getInstance() {
    if (marketDataServiceHolder == null) {
      marketDataServiceHolder = new MarketDataService();
    }

    return marketDataServiceHolder;
  }

  public ArrayList<Snapshot> getTimeSeries(Ticker ticker) {
    synchronized (simulateData) {
      return simulateData.getTimeSeries(ticker).getSnapshots();
    }
  }

  public Snapshot getLatestSnapshot(Ticker ticker) {
    synchronized (simulateData) {
      return simulateData.getTimeSeries(ticker).getLatestSnapshot();
    }
  }
}
