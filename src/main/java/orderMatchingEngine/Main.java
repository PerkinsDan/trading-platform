package orderMatchingEngine;

import java.util.concurrent.PriorityBlockingQueue;

public class Main {
    public static void main(String[] args) {

        OrderProcessor processor = new OrderProcessor();
        PriorityBlockingQueue<Order> orderQueue = OrderProcessor.getOrderQueue();
        MatchingEngine engine = new MatchingEngine(orderQueue);
        

        // Start matching engine in a separate thread
        new Thread(engine).start();

        // Simulate incoming orders
        processor.submitOrder(new Order(Order.Type.BUY, 100.5, 10));
        processor.submitOrder(new Order(Order.Type.SELL, 100.0, 5));
        processor.submitOrder(new Order(Order.Type.SELL, 99.5, 10));
        processor.submitOrder(new Order(Order.Type.BUY, 101.0, 15));

        // Allow time for execution
        try { Thread.sleep(2000); } catch (InterruptedException e) { }
        processor.shutdown();
    }
}
