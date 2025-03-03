package orderMatchingEngine;

import java.util.PriorityQueue;

public class TradeBook {
    private final String ticker;
    private final PriorityQueue<Order> buyBook;
    private final PriorityQueue<Order> sellBook;

    public TradeBook(String ticker){
        this.buyBook = new PriorityQueue<>( 100,new BuyOrderComparator());
        this.sellBook = new PriorityQueue<>(100, new SellOrderComparator());
        this.ticker = ticker;
    }

    public void addToBook(Order order){
        switch(order.getType()){
            case BUY:
                buyBook.offer(order);
                break;
            case SELL:
                sellBook.offer(order);
                break;
        }
    }

    public PriorityQueue<Order> getBuyBook(){
        return buyBook;
    }
    public PriorityQueue<Order> getSellBook(){
        return sellBook;
    }
    public void printOrders() {
        System.out.println("\nBuy Orders:");
        buyBook.forEach(System.out::println);
        System.out.println("Sell Orders:");
        sellBook.forEach(System.out::println);
    }
        
    
}
