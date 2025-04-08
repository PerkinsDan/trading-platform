package com.setap.marketdata;

import static com.setap.marketdata.Tickers.AAPL;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TimeSeriesTest {

  TimeSeries timeSeries;

  @BeforeEach
  void setUp() {
    timeSeries = new TimeSeries();
  }

  @Test
  void addSnapshotShouldAddSnapshotToList() {
    Snapshot snapshot = new Snapshot(AAPL, 150.0, LocalTime.now());
    timeSeries.addSnapshot(snapshot);
    assertEquals(1, timeSeries.getSnapshots().size());
    assertEquals(snapshot, timeSeries.getSnapshots().getFirst());
  }

  @Test
  void getLatestSnapshotShouldReturnNullForEmptyList() {
    assertNull(timeSeries.getLatestSnapshot());
  }

  @Test
  void getLatestSnapshotShouldReturnLastSnapshot() {
    Snapshot snapshot1 = new Snapshot(AAPL, 150.0, LocalTime.now());
    Snapshot snapshot2 = new Snapshot(AAPL, 155.0, LocalTime.now());
    timeSeries.addSnapshot(snapshot1);
    timeSeries.addSnapshot(snapshot2);
    assertEquals(snapshot2, timeSeries.getLatestSnapshot());
  }

  @Test
  void getLatestChangeShouldReturnZeroForLessThanTwoSnapshots() {
    Snapshot snapshot = new Snapshot(AAPL, 150.0, LocalTime.now());
    timeSeries.addSnapshot(snapshot);
    assertEquals(0, timeSeries.getLatestChange());
  }

  @Test
  void getLatestChangeShouldReturnCorrectPercentageChange() {
    Snapshot snapshot1 = new Snapshot(AAPL, 150.0, LocalTime.now());
    Snapshot snapshot2 = new Snapshot(AAPL, 165.0, LocalTime.now());
    timeSeries.addSnapshot(snapshot1);
    timeSeries.addSnapshot(snapshot2);
    assertEquals(10.0, timeSeries.getLatestChange());
  }
}
