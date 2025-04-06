package com.setap.marketdata;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;

public class SnapshotTest {

  @Test
  void snapshotShouldReturnCorrectTicker() {
    Snapshot snapshot = new Snapshot("AAPL", 150.0, LocalTime.now());
    assertEquals("AAPL", snapshot.getTicker());
  }

  @Test
  void snapshotShouldReturnCorrectPrice() {
    Snapshot snapshot = new Snapshot("AAPL", 150.0, LocalTime.now());
    assertEquals(150.0, snapshot.getPrice());
  }

  @Test
  void snapshotShouldReturnCorrectTimestamp() {
    LocalTime now = LocalTime.now();
    Snapshot snapshot = new Snapshot("AAPL", 150.0, now);
    assertEquals(now, snapshot.getTimestamp());
  }

  @Test
  void snapshotToStringShouldReturnFormattedString() {
    LocalTime now = LocalTime.now();
    Snapshot snapshot = new Snapshot("AAPL", 150.0, now);
    String expected =
      "Snapshot{ticker='AAPL', price=150.0, timestamp=" + now + "}";
    assertEquals(expected, snapshot.toString());
  }
}
