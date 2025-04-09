package com.setap.marketdata.simulatedata;

import java.time.LocalTime;

public record Snapshot(double price, LocalTime timestamp, double change) {}
