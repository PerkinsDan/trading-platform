package orderMatchingEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class MatchingEngine{
    
    private static final Map<String, TradeBook> TradeBookMap = new HashMap();
    private static final String[] equities = {"A","B","C","D","E"};

    public MatchingEngine(){
        for(String equity : equities){
            AddTradeBook(equity);
        }
            
    }

    private static void AddTradeBook(String ticker){
        TradeBook newTradeBook = new TradeBook(ticker);
        TradeBookMap.put(ticker,newTradeBook);
    }

    public static TradeBook getTradeBook(String equity){
        return TradeBookMap.get(equity);
    }

    public static int match(TradeBook book){
        PriorityQueue<Order> buyOrders = book.getBuyBook();
        PriorityQueue<Order> sellOrders = book.getSellBook();

        int matchesFound = 0;

        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order buy = buyOrders.peek();
            Order sell = sellOrders.peek();

            if (buy.getPrice() >= sell.getPrice()) { // Match found
                int quantityTraded = Math.min(buy.getQuantity(), sell.getQuantity());

                System.out.println("Trade Executed: " + quantityTraded + " " + buy.getTicker() + " @ " + sell.getPrice());
                //replace this with some kind of message class to notify API of whats changed
                matchesFound++;

                // Reduce quantities
                buy.reduceQuantity(quantityTraded);
                sell.reduceQuantity(quantityTraded);

                // Remove completed orders
                if (buy.getQuantity() == 0) buyOrders.poll();
                if (sell.getQuantity() == 0) sellOrders.poll();
            } else {
                break; // No more matches possible
            }
        }
        return matchesFound; // True if at least one match occurred, false otherwise
    }


}