package com.tradingplatform.LiquidityEngine;

import com.tradingplatform.orderprocessor.orders.*;

import org.apache.commons.math3.distribution.NormalDistribution;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Random;

public class MockOrderGenerator{
    
    private final Random random = new Random();


    protected MockOrderGenerator(){
    }

    private OrderType randomOrderType(){
        return (random.nextInt(2)%2 == 0) ? OrderType.BUY : OrderType.SELL;
      
    }
    
    private double randomPrice(double rootPrice){
        // TODO: hit market data endpoint  and return double for current price
        return new NormalDistribution(rootPrice, 0.25*rootPrice).sample();
    }
        
    private Ticker randomTicker(){
        Ticker[] tickers = Ticker.values();
        return Ticker.values()[random.nextInt(tickers.length)];
    }

    private int randomQuantity(){
        return random.nextInt(5000)+1;
    }

    public String generateMockOrder(double rootPrice){
        Order order  = new Order(randomOrderType(), randomTicker(), randomPrice(rootPrice),randomQuantity(), "LoadTester");
        System.out.println("Mock order created:" + order.toString());
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            System.err.printf(
                "Exception converting random order to JSON %s",
                e.getMessage()
              );
            return "";
        }
    }


}