package com.setap.marketdata;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MarketDataServiceTest {

  private MarketDataService marketDataService;

  @BeforeEach
  void setUp() throws InterruptedException {
    ArrayList<String> tickers = new ArrayList<>();
    tickers.add("AAPL");

    marketDataService = new MarketDataService(tickers);
    Thread.sleep(100); // Wait for the data generation thread to start
  }

  @Test
  void getTimeSeriesShouldReturnPopulatedListAfterDataGeneration() {
    ArrayList<Snapshot> snapshots = marketDataService.getTimeSeries("AAPL");
    assertFalse(snapshots.isEmpty());
  }

  @Test
  void getLatestSnapshotShouldReturnLastSnapshotAfterDataGeneration() {
    assertNotNull(marketDataService.getLatestSnapshot("AAPL"));
  }

  @Test
  void getLatestChangeShouldReturnNonZeroAfterDataGeneration() {
    assertNotEquals(0, marketDataService.getLatestChange("AAPL"));
  }
}
