package com.setap.marketdata;

public class MarketDataServiceMain {

  static MarketDataService marketDataService;

  public MarketDataServiceMain() {
    marketDataService = new MarketDataService();
    marketDataService.start();
  }

  public String getData(String symbol) {
    try {
      Tickers ticker = Tickers.valueOf(symbol);
      return marketDataService.getTickerDataFromMap(ticker);
    } catch (IllegalArgumentException e) {
      return "Invalid symbol";
    }
  }
}
