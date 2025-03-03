package orderMatchingEngine;

import java.util.concurrent.PriorityBlockingQueue;

public class Main {
    public static void main(String[] args) {
        MatchingEngine engine = new MatchingEngine();
        OrderProcessor processor = new OrderProcessor();
        Order[] orders = {
            new Order(Order.Type.SELL, Order.Ticker.A, 99.0, 575),
            new Order(Order.Type.SELL, Order.Ticker.A, 100.0, 500),
            new Order(Order.Type.SELL, Order.Ticker.A, 100.0, 50),
            new Order(Order.Type.SELL, Order.Ticker.A, 101.5, 150),
            new Order(Order.Type.BUY, Order.Ticker.A, 98.0, 30),
            new Order(Order.Type.BUY, Order.Ticker.A, 100.0, 700),
            new Order(Order.Type.BUY, Order.Ticker.A, 100.0, 300),
            new Order(Order.Type.BUY, Order.Ticker.A, 102.0, 130)
            };
    
        for(Order order : orders){
            processor.addOrder(order);
        }

        long startTime = System.currentTimeMillis();
        MatchingEngine.match(MatchingEngine.getTradeBook("A"));
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        long average = duration/5;
        System.out.println("5 trades matched in " + duration + " milliseconds.");
        System.out.println(average + " milliseconds per match");


    }
}
