package orderMatchingEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


public class MatchingEngine{
    
    private static final Map<Order.Ticker, TradeBook> TradeBookMap = new HashMap<>();
    private static final Order.Ticker[] equities = {Order.Ticker.A,Order.Ticker.B,Order.Ticker.C,Order.Ticker.D,Order.Ticker.E};

    public MatchingEngine(){
        for(Order.Ticker equity : equities){
            AddTradeBook(equity);
        }
            
    }

    private static void AddTradeBook(Order.Ticker ticker){
        TradeBook newTradeBook = new TradeBook(ticker);
        TradeBookMap.put(ticker,newTradeBook);
    }

    public static TradeBook getTradeBook(Order.Ticker ticker){
        return TradeBookMap.get(ticker);
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