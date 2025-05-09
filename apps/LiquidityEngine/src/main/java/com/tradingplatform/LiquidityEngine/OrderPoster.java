package com.tradingplatform.LiquidityEngine;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

import com.tradingplatform.orderprocessor.orders.Ticker;

import io.github.cdimascio.dotenv.Dotenv;

public class OrderPoster{

    private final Random random = new Random();
    Dotenv dotenv = Dotenv.configure().directory("/home/asad/IOT/trading-platform/apps/LiquidityEngine/.env").load();

    private MockOrderGenerator generator;
    private int frequency; // number of orders submitted per minute
    private HttpClient client;
    
    public OrderPoster(int frequency){
        this.frequency = frequency;
        this.generator = new MockOrderGenerator();
        client = HttpClient.newHttpClient();
    }

    private Ticker randomTicker(){
        Ticker[] tickers = Ticker.values();
        return tickers[random.nextInt(tickers.length)];
    }

    private HttpRequest createRequestForRandomOrder(String ticker){ 
        return HttpRequest.newBuilder()
            .uri(URI.create(dotenv.get("BASE_URL_DEV") + "orders/create"))
            .POST(HttpRequest.BodyPublishers.ofString(generator.generateMockOrderAsJson(ticker)))
            .build();      
    }

    public void postOrder(){
            String ticker = randomTicker().toString();
            client = HttpClient.newHttpClient();
            long start = System.currentTimeMillis();
            HttpRequest request = createRequestForRandomOrder(ticker);
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if(response.statusCode() == 201){
                    System.out.println("Sucessfully placed order: " +request);
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Http request experienced an error while submitting order");
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println(request.toString());
            System.out.printf("LiquidityEngine : Order processed in %d milliseconds.", (end-start));
            Thread.sleep((60*1000)/frequency);
    }
}