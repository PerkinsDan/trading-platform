package com.tradingplatform.LiquidityEngine;

import com.tradingplatform.orderprocessor.orders.*;

import io.github.cdimascio.dotenv.Dotenv;

import org.apache.commons.math3.distribution.NormalDistribution;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MockOrderGenerator{
    
    private final Random random = new Random();
    private final ObjectMapper mapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();


    protected MockOrderGenerator(){
    }

    private double pingMarketDataForPrice(String ticker){
        Dotenv dotenv = Dotenv.configure().load();
        String url = dotenv.get("BASE_URL_MARKET_DATA_DEV") + "latest-snapshot?ticker=" + ticker;
        HttpRequest request = HttpRequest.newBuilder()
                                        .uri(URI.create(url))
                                        .GET() 
                                        .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String reponseString = response.body();
            JsonNode node = mapper.readTree(reponseString);
            return Double.parseDouble(node.get("price").asText());
        } catch (IOException | InterruptedException e) {
            return -1;
        }
    }

    private OrderType randomOrderType(){
        return (random.nextInt(2)%2 == 0) ? OrderType.BUY : OrderType.SELL; 
    }
    
    private double randomPrice(double rootPrice){
        return new NormalDistribution(rootPrice, 0.25*rootPrice).sample();
    }

    private String randomUser(){
        String[] users = {"userA","userB"};
        return users[random.nextInt(users.length)];
    }

    private int randomQuantity(){
        return random.nextInt(500)+1;
    }

    public String generateMockOrderAsJson(String ticker){

        Double rootPrice = pingMarketDataForPrice(ticker);

        if (rootPrice == -1){
            return "Error while retrieving market price from MarketData.";
        }

        Order order  = new Order(randomOrderType(), randomUser(), Ticker.valueOf(ticker), randomPrice(rootPrice),randomQuantity());
        System.out.println("Mock order created:" + order.toString());
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            return "Error while converting to JSON"    ;
        }
    }


}