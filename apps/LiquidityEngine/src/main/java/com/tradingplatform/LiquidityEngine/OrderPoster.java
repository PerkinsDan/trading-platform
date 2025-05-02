package com.tradingplatform.LiquidityEngine;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

import com.tradingplatform.orderprocessor.orders.Ticker;

public class OrderPoster{

    private final Random random = new Random();

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
            .uri(URI.create(System.getenv("BASE_URL" + "orders/create")))
            .POST(HttpRequest.BodyPublishers.ofString(generator.generateMockOrderAsJson(ticker)))
            .build();      
    }
    public void submitOrder(String body){

    }

    private void postOrder(){
            String ticker = randomTicker().toString();
            client = HttpClient.newHttpClient();
            long start = System.currentTimeMillis();
            try {
                HttpRequest request = createRequestForRandomOrder(ticker)
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if(response.statusCode() == 201){
                    System.out.println("Sucessfully placed order: " +request);
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Http request experienced an error");
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.printf("LiquidityEngine : Order processed in %d milliseconds.", (end-start));
    }

    public void startOrderStream() throws IOException, InterruptedException{
        while(true){
            postOrder();
        }
    }
}