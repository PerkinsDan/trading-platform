package com.setap.marketdata;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class MarketDataFetcher {

  private static final String API_KEY = "J3W4Q7FUF7FTFCYM";
  private static final String API_URL =
    "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=%s&interval=5min&apikey=" +
    API_KEY;

  public static Map<String, String> fetchData() {
    Map<String, String> marketData = new HashMap<>();
    for (Tickers ticker : Tickers.values()) {
      String tickerData = fetchTickerData(ticker);

      if (tickerData != null) {
        marketData.put(ticker.toString(), tickerData);
      }
    }

    return marketData;
  }

  private static String fetchTickerData(Tickers ticker) {
    String url = String.format(API_URL, ticker.toString());

    try (HttpClient client = HttpClient.newHttpClient()) {
      HttpRequest request = HttpRequest.newBuilder()
        .uri(new URI(url))
        .GET()
        .build();

      HttpResponse<String> response = client.send(
        request,
        HttpResponse.BodyHandlers.ofString()
      );

      System.out.println(response.body());
      return response.body();
    } catch (IOException | InterruptedException | URISyntaxException e) {
      e.printStackTrace();
      return null;
    }
  }
}
