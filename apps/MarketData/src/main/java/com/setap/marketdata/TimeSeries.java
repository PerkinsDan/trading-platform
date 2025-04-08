package com.setap.marketdata;

import java.time.LocalTime;
import java.util.ArrayList;

public class TimeSeries {

  private final ArrayList<Snapshot> snapshots;

  public TimeSeries() {
    this.snapshots = new ArrayList<>();
  }

  public void addSnapshot(Snapshot snapshot) {
    this.snapshots.add(snapshot);
  }

  public ArrayList<Snapshot> getSnapshots() {
    LocalTime currentTime = LocalTime.now();

    ArrayList<Snapshot> filteredSnapshots = new ArrayList<>(snapshots);

    filteredSnapshots.removeIf(snapshot ->
      snapshot.getTimestamp().isAfter(currentTime)
    );

    return filteredSnapshots;
  }

  public Snapshot getLatestSnapshot() {
    if (snapshots.isEmpty()) {
      return null;
    }

    LocalTime currentTime = LocalTime.now();

    Snapshot latest = null;
    for (Snapshot snapshot : snapshots) {
      if (snapshot.getTimestamp().isBefore(currentTime)) {
        latest = snapshot;
      }
    }

    return latest;
  }

  public double getLatestChange() {
    if (snapshots.size() < 2) {
      return 0;
    }

    LocalTime currentTime = LocalTime.now();
    ArrayList<Snapshot> filteredSnapshots = new ArrayList<>(snapshots);
    filteredSnapshots.removeIf(snapshot ->
      snapshot.getTimestamp().isAfter(currentTime)
    );

    Snapshot latest = filteredSnapshots.getLast();
    Snapshot previous = filteredSnapshots.get(filteredSnapshots.size() - 2);

    return (
      ((latest.getPrice() - previous.getPrice()) / previous.getPrice()) * 100
    );
  }
}
