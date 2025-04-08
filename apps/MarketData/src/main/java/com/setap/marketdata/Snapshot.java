package com.setap.marketdata;

import java.time.LocalTime;

public record Snapshot(double price, LocalTime timestamp, double change) {}
