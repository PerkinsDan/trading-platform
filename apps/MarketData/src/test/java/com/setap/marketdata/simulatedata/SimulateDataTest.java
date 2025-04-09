package com.setap.marketdata.simulatedata;

import static com.setap.marketdata.constants.Tickers.AAPL;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimulateDataTest {

  private SimulateData simulateData;

  @BeforeEach
  void setUp() {
    simulateData = new SimulateData();
  }

  @Test
  void getTimeSeriesShouldReturnEmptyTimeSeriesForNewTicker() {
    TimeSeries timeSeries = simulateData.getTimeSeries(AAPL);
    assertNotNull(timeSeries);
    assertTrue(timeSeries.getSnapshots().isEmpty());
  }

  @Test
  void generateDataShouldPopulateTimeSeriesWithSnapshots() {
    simulateData.generateData();
    TimeSeries timeSeries = simulateData.getTimeSeries(AAPL);
    assertFalse(timeSeries.getSnapshots().isEmpty());
  }

  @Test
  void generateDataShouldGenerateNonNegativePrices() {
    simulateData.generateData();
    TimeSeries timeSeries = simulateData.getTimeSeries(AAPL);
    assertTrue(
      timeSeries
        .getSnapshots()
        .stream()
        .allMatch(snapshot -> snapshot.price() >= 0)
    );
  }

  @Test
  void generateDataShouldGenerateSnapshotsWithinMarketHours() {
    simulateData.generateData();
    TimeSeries timeSeries = simulateData.getTimeSeries(AAPL);
    assertTrue(
      timeSeries
        .getSnapshots()
        .stream()
        .allMatch(
          snapshot ->
            !snapshot.timestamp().isBefore(LocalTime.of(9, 30)) &&
            !snapshot.timestamp().isAfter(LocalTime.of(16, 0))
        )
    );
  }
}
