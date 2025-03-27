package com.setap.marketdata;

import java.util.Map;

public class MarketDataService {

  private Map<String, String> marketData;

  public MarketDataService() {
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

  public String getTickerDataFromMap(String symbol) {
    try {
      Tickers ticker = Tickers.valueOf(symbol);
      return marketData.get(ticker.toString());
    } catch (IllegalArgumentException e) {
      return "Invalid symbol";
    }
  }
}
