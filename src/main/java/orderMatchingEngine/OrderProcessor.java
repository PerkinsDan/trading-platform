package orderMatchingEngine;

import java.util.concurrent.*;

public class OrderProcessor{

    //Singleton style - we definitely only ever want one orderQueue
    private static PriorityBlockingQueue<Order> orderQueue = null; // thread safe queue, supports take() which checks that the queue is empty before doing anything, and will wait if it is
    private final ExecutorService executor; // managed thread pool, to support order coming from multiple places, potentially can be used to limit num. of concurrent connections.
     
    public void submitOrder (Order order){
        orderQueue.offer(order);
    }

    public OrderProcessor(){
        executor = Executors.newFixedThreadPool(4);
        if(orderQueue == null){
            orderQueue = new PriorityBlockingQueue<Order>(100, new timeBasedComparator());  
        } 
    }

    public static PriorityBlockingQueue<Order> getOrderQueue(){
        return orderQueue;
    }
  
    public void shutdown(){
        executor.shutdown();
    }

    public Boolean isOrderProcessorShutDown(){
        return executor.isShutdown() || executor.isTerminated();
    }
        

}