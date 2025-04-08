package com.setap.marketdata;

import static com.setap.marketdata.Tickers.AAPL;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SimulatedDataTest {

  private SimulatedData simulatedData;

  @BeforeEach
  void setUp() {
    simulatedData = new SimulatedData();
  }

  @Test
  void getTimeSeriesShouldReturnEmptyTimeSeriesForNewTicker() {
    TimeSeries timeSeries = simulatedData.getTimeSeries(AAPL);
    assertNotNull(timeSeries);
    assertTrue(timeSeries.getSnapshots().isEmpty());
  }

  @Test
  void generateDataShouldPopulateTimeSeriesWithSnapshots() {
    simulatedData.generateData();
    TimeSeries timeSeries = simulatedData.getTimeSeries(AAPL);
    assertFalse(timeSeries.getSnapshots().isEmpty());
  }

  @Test
  void generateDataShouldGenerateNonNegativePrices() {
    simulatedData.generateData();
    TimeSeries timeSeries = simulatedData.getTimeSeries(AAPL);
    assertTrue(
      timeSeries
        .getSnapshots()
        .stream()
        .allMatch(snapshot -> snapshot.getPrice() >= 0)
    );
  }

  @Test
  void generateDataShouldGenerateSnapshotsWithinMarketHours() {
    simulatedData.generateData();
    TimeSeries timeSeries = simulatedData.getTimeSeries(AAPL);
    assertTrue(
      timeSeries
        .getSnapshots()
        .stream()
        .allMatch(
          snapshot ->
            !snapshot.getTimestamp().isBefore(LocalTime.of(9, 30)) &&
            !snapshot.getTimestamp().isAfter(LocalTime.of(16, 0))
        )
    );
  }
}
