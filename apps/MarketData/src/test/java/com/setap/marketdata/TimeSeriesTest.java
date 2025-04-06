package com.setap.marketdata;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;

public class TimeSeriesTest {

  @Test
  void addSnapshotShouldAddSnapshotToList() {
    TimeSeries timeSeries = new TimeSeries();
    Snapshot snapshot = new Snapshot("AAPL", 150.0, LocalTime.now());
    timeSeries.addSnapshot(snapshot);
    assertEquals(1, timeSeries.getSnapshots().size());
    assertEquals(snapshot, timeSeries.getSnapshots().getFirst());
  }

  @Test
  void getLatestSnapshotShouldReturnNullForEmptyList() {
    TimeSeries timeSeries = new TimeSeries();
    assertNull(timeSeries.getLatestSnapshot());
  }

  @Test
  void getLatestSnapshotShouldReturnLastSnapshot() {
    TimeSeries timeSeries = new TimeSeries();
    Snapshot snapshot1 = new Snapshot("AAPL", 150.0, LocalTime.now());
    Snapshot snapshot2 = new Snapshot("AAPL", 155.0, LocalTime.now());
    timeSeries.addSnapshot(snapshot1);
    timeSeries.addSnapshot(snapshot2);
    assertEquals(snapshot2, timeSeries.getLatestSnapshot());
  }

  @Test
  void getLatestChangeShouldReturnZeroForLessThanTwoSnapshots() {
    TimeSeries timeSeries = new TimeSeries();
    Snapshot snapshot = new Snapshot("AAPL", 150.0, LocalTime.now());
    timeSeries.addSnapshot(snapshot);
    assertEquals(0, timeSeries.getLatestChange());
  }

  @Test
  void getLatestChangeShouldReturnCorrectPercentageChange() {
    TimeSeries timeSeries = new TimeSeries();
    Snapshot snapshot1 = new Snapshot("AAPL", 150.0, LocalTime.now());
    Snapshot snapshot2 = new Snapshot("AAPL", 165.0, LocalTime.now());
    timeSeries.addSnapshot(snapshot1);
    timeSeries.addSnapshot(snapshot2);
    assertEquals(10.0, timeSeries.getLatestChange());
  }
}
