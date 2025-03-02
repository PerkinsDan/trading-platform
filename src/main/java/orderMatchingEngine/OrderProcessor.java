package main.java.orderMatchingEngine;

import java.util.concurrent.*;

public class OrderProcessor{

    //Singleton style - we definitely only ever want one orderQueue
    private static final BlockingQueue<Order> orderQueue = null; // thread safe queue, supports take() which checks that the queue is empty before doing anything, and will wait if it is
    private final ExecutorService executor = Executors.newFixedThreadPool(4); // managed thread pool, to support order coming from multiple places, potentially can be used to limit num. of concurrent connections.

    public void submitOrder (Order order){
        orderQueue.offer(order);
    }

    public static BlockingQueue<Order> getOrderQueueInstance(){
        if(orderQueue = null){
            orderQueue  = new LinkedBlockingQueue()
            return orderQueue;
        } else {
            return orderQueue;
        }
    }

    public void shutDown(){
        executor.shutdown();
    }
        

}