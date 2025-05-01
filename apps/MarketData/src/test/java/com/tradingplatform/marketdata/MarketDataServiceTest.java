package com.tradingplatform.marketdata;

import static com.tradingplatform.marketdata.constants.Ticker.AAPL;
import static org.junit.jupiter.api.Assertions.*;

import com.tradingplatform.marketdata.simulatedata.Snapshot;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MarketDataServiceTest {

  private MarketDataService marketDataService;

  @BeforeEach
  void setUp() throws InterruptedException {
    marketDataService = MarketDataService.getInstance();
    Thread.sleep(100); // Wait for the data generation thread to start
  }

  @Test
  void getTimeSeriesShouldReturnPopulatedListAfterDataGeneration() {
    ArrayList<Snapshot> snapshots = marketDataService.getTimeSeries(AAPL);
    assertFalse(snapshots.isEmpty());
  }

  @Test
  void getLatestSnapshotShouldReturnLastSnapshotAfterDataGeneration() {
    assertNotNull(marketDataService.getLatestSnapshot(AAPL));
  }
}
