package com.setap.marketdata;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MarketDataServiceTest {

  private MarketDataService marketDataService;

  @BeforeEach
  void setUp() {
    marketDataService = new MarketDataService();
  }

  @Test
  void getTimeSeriesShouldReturnPopulatedListAfterDataGeneration() {
    marketDataService.getTimeSeries(Tickers.AAPL);
    ArrayList<Snapshot> snapshots = marketDataService.getTimeSeries(
      Tickers.AAPL
    );
    assertFalse(snapshots.isEmpty());
  }

  @Test
  void getLatestSnapshotShouldReturnLastSnapshotAfterDataGeneration() {
    marketDataService.getTimeSeries(Tickers.AAPL);
    assertNotNull(marketDataService.getLatestSnapshot(Tickers.AAPL));
  }

  @Test
  void getLatestChangeShouldReturnNonZeroAfterDataGeneration() {
    marketDataService.getTimeSeries(Tickers.AAPL);
    assertNotEquals(0, marketDataService.getLatestChange(Tickers.AAPL));
  }
}
