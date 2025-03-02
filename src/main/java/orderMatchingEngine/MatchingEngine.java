package main.java.orderMatchingEngine;

import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;

public class MatchingEngine implements Runnable{

    private final BlockingQueue<Order> orderQueue;
    private final PriorityQueue<Order> buyBook = new PriorityQueue<>( 100,new BuyOrderComparator());
    private final PriorityQueue<Order> sellBook = new PriorityQueue<>(100, new SellOrderComparator());

    public MatchingEngine(BlockingQueue<Order> orderQueue){
        this.orderQueue = orderQueue;
    }

    @Override
    public void run(){
        while(true){
            try{
                Order order = orderQueue.take();
                MatchOrder(order);

            } catch(InterruptedException e){
                Thread.currentThread().interrupt();
                e.printStackTrace();
                break;
            }

        }
    }

    private void MatchOrder(Order order){
        switch (order.getType()){
            case BUY:
                processBuy(order);
            case SELL:
                processSell(order);
            default:
                break;
        }
    }

    private void processBuy(Order order){
        //fill n put: fill an order as much as possible, and put the remaining on the books

        

    }

    private void processSell(Order order){
        //fill n put: fill an order as much as possible, and put the remaining on the books

    }
            

    


}