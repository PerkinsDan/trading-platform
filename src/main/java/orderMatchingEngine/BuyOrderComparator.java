package main.java.orderMatchingEngine;

import java.util.Comparator;

public class BuyOrderComparator implements Comparator<Order>{
    @Override
    public int compare(Order order1, Order order2){
        // if price not equal return higher price first, most desperate to buy at the top
        if(order1.getPrice() != order2.getPrice()){
            return Double.compare(order2.getPrice(), order1.getPrice());
        }
        // if the price is equal then return lower timestamp first becuase that order was submitted first
        return Long.compare(order1.getTimestamp(), order2.getTimestamp());
    }
}