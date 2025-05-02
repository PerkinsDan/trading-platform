package com.tradingplatform.LiquidityEngine;

import com.tradingplatform.orderprocessor.orders.*;

import org.apache.commons.math3.distribution.NormalDistribution;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Random;
import java.util.List;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MockOrderGenerator{
    
    private final Random random = new Random();


    protected MockOrderGenerator(){
    }

    private double pingMarketDataForPrice(String ticker){
        String url = System.getenv("BASE_URL_MARKET_DATA_DEV") + "latest-snapshot?ticker=" + ticker;
        HttpRequest request = HttpRequest.newBuilder()
                                        .uri(URI.create(url)) // Set the URL
                                        .GET() // Specify this as a GET request
                                        .build();
        // Create an HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Send the GET request and get the response
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Double.parseDouble(response.body());
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1; 
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
        return tickers[random.nextInt(tickers.length)];
    }

    private String randomUser(){
        String[] users = {"userA","userB"};
        return users[random.nextInt(users.length)];
    }

    private int randomQuantity(){
        return random.nextInt(5000)+1;
    }

    public String generateMockOrderAsJson(String Ticker){
        Double rootPrice = pingMarketDataForPrice(Ticker);
        if (rootPrice == -1){
            System.out.println("MarketData is flopping rn icl cant publish order via liquidity engine");
        }

        Order order  = new Order(randomOrderType(), randomUser(), randomTicker(), randomPrice(rootPrice),randomQuantity());
        System.out.println("Mock order created:" + order.toString());
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            System.out.println("Error convering to JSON");
            return "";
        }
    }


}