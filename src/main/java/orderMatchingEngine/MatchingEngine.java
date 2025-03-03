package orderMatchingEngine;

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
                BuyAtMarketBest(order);
            case SELL:
                SellAtMarketBest(order);
            default:
                break;
        }
    }

    private void BuyAtMarketBest(Order order){
        int buyAmount = order.getQuantity();
        Order topOrder = sellBook.peek();
    
        if(topOrder == null || order.getPrice() < topOrder.getPrice()){
            // sellbook was empty, add this order to the book and move on
            // OR the buy price was just too low and no one is interested in selling at this price, add this order to the book and move on
            sellBook.offer(order);
        } else {
            //topOrder is not null and prices overlap so proceed to try matching
            Double topPrice = topOrder.getPrice();
            int topQuantity = topOrder.getQuantity();
            while (buyAmount != 0){
                if(buyAmount >= topQuantity){
                    //matched but not enough quantity to fill, fill as much as possible and move on
                    System.out.println(topOrder.getQuantity() + " lots bought at " +  topPrice);
                    buyAmount = buyAmount - topQuantity;
                    Order filledOrder = sellBook.poll();
                    System.out.println("Order with id = " + filledOrder.getId() + "has been filled and removed from the books");
                    //set new best price and quantity
                    topPrice = newBestPrice();
                    topQuantity = newBestQuantity();
                } else {
                    System.out.println(buyAmount + "lots bought at " + topPrice );
                    topOrder.setQuantity(topQuantity - buyAmount);
                    buyAmount = 0; //set to zero to break out of loop as we've filled the order
                }         
            }
        }
    }

    private void SellAtMarketBest(Order order){
        //sell at market best
        int sellAmount = order.getQuantity();
        Order topOrder = buyBook.peek();
    
        if(topOrder == null || order.getPrice() > topOrder.getPrice()){
            // buybook was empty, add this order to the book and move on
            // OR the sell price was just too high and no one is interested in buying at this price, add this order to the book and move on
            buyBook.offer(order);
        }
        //topOrder is not null and prices overlap so proceed to try matching        int topQuantity = topOrder.getQuantity();
        while (sellAmount != 0){
            if(sellAmount >= topQuantity){
                //matched but not enough quantity to fill, fill as much as possible adust for whats left and move on
                System.out.println(topOrder.getQuantity() + " lots sold at " +  topPrice);
                sellAmount = sellAmount - topQuantity;
                Order filledOrder = sellBook.poll();
                System.out.println("Order with id = " + filledOrder.getId() + "has been filled and removed from the books");
                //set new best price and quantity
                topPrice = newBestPrice();
                topQuantity = newBestQuantity();
            } else {
                //matched and enough quantity to fill  
                System.out.println(sellAmount + "lots sold at " + topPrice );
                topOrder.setQuantity(topQuantity - sellAmount);
                sellAmount = 0; //set to zero to break out of loop as we've filled the order
            }         
        }
    }
    
    private Double newBestPrice(){
        return sellBook.peek().getPrice();
    }
    private int newBestQuantity(){
        return sellBook.peek().getQuantity();
    }

    


}