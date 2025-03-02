package main.java.orderMatchingEngine;

import java.util.concurrent.atomic.AtomicLong;

public class Order {
    // automatic UUID generator for setting the order type
    private final AtomicLong counter = new AtomicLong(0);
    
    enum Type { BUY, SELL }
    
    private final long id;
    private final Type type;
    private final double price;
    private final int quantity;
    private final long timestamp;

    public Order(Type type, double price, int quantity) {
        this.id = counter.getAndIncrement();
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = System.nanoTime(); // FIFO tie-breaker
    }

    public long getId() { return id; }
    public Type getType() { return type; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public long getTimestamp() { return timestamp; }
}
