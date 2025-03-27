package com.setap.marketdata;

import java.util.Map;

public class MarketDataService {

  private Map<String, String> marketData;

  public void start() {
    System.out.println("MarketDataService started!");

    while (true) {
      fetchData();

      try {
        Thread.sleep(60000); // Sleep for 1 minute
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.out.println(
          "Thread was interrupted, Failed to complete operation"
        );
      }
    }
  }

  private void fetchData() {
    marketData = MarketDataFetcher.fetchData();
  }

  public String getTickerDataFromMap(Tickers ticker) {
    return marketData.get(ticker.toString());
  }
}
