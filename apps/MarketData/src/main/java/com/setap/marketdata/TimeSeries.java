package com.setap.marketdata;

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
    return snapshots;
  }

  public Snapshot getLatestSnapshot() {
    if (snapshots.isEmpty()) {
      return null;
    }
    return snapshots.getLast();
  }

  public double getLatestChange() {
    if (snapshots.size() < 2) {
      return 0;
    }

    Snapshot latest = snapshots.getLast();
    Snapshot previous = snapshots.get(snapshots.size() - 2);

    return (
      ((latest.getPrice() - previous.getPrice()) / previous.getPrice()) * 100
    );
  }
}
