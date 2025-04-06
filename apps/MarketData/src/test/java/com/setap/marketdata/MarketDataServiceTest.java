package com.setap.marketdata;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MarketDataServiceTest {

  private MarketDataService marketDataService;

  @BeforeEach
  void setUp() throws InterruptedException {
    marketDataService = new MarketDataService();
    Thread.sleep(100); // Wait for the data generation thread to start
  }

  @Test
  void getTimeSeriesShouldReturnPopulatedListAfterDataGeneration() {
    ArrayList<Snapshot> snapshots = marketDataService.getTimeSeries(
      Tickers.AAPL
    );
    assertFalse(snapshots.isEmpty());
  }

  @Test
  void getLatestSnapshotShouldReturnLastSnapshotAfterDataGeneration() {
    assertNotNull(marketDataService.getLatestSnapshot(Tickers.AAPL));
  }

  @Test
  void getLatestChangeShouldReturnNonZeroAfterDataGeneration() {
    assertNotEquals(0, marketDataService.getLatestChange(Tickers.AAPL));
  }
}
