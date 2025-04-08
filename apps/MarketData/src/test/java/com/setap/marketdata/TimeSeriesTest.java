package com.setap.marketdata;

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
    Snapshot snapshot = new Snapshot(150.0, LocalTime.now(), 0);
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
    Snapshot snapshot1 = new Snapshot(150.0, LocalTime.now(), 0);
    Snapshot snapshot2 = new Snapshot(150.0, LocalTime.now(), 0);
    timeSeries.addSnapshot(snapshot1);
    timeSeries.addSnapshot(snapshot2);
    assertEquals(snapshot2, timeSeries.getLatestSnapshot());
  }
}
