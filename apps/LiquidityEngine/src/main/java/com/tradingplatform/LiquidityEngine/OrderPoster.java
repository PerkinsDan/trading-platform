package com.tradingplatform.LiquidityEngine;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import com.tradingplatform.orderprocessor.orders.Ticker;

import io.github.cdimascio.dotenv.Dotenv;

public class OrderPoster{

    private Thread orderThread;

    private final Random random = new Random();

    private MockOrderGenerator generator;
    private int frequency; // number of orders submitted per minute
    private HttpClient client;
    private AtomicBoolean running = new AtomicBoolean(false);
    
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
        Dotenv dotenv = Dotenv.configure().directory("/home/asad/IOT/trading-platform/apps/LiquidityEngine/.env").load();
        String url = dotenv.get("BASE_URL_DEV") + "orders/create";
        return HttpRequest.newBuilder()
            .uri(URI.create(dotenv.get("BASE_URL_DEV") + "orders/create"))
            .POST(HttpRequest.BodyPublishers.ofString(generator.generateMockOrderAsJson(ticker)))
            .build();      
    }

    public void postOrder() throws InterruptedException{
            String ticker = randomTicker().toString();
            client = HttpClient.newHttpClient();
            long start = System.currentTimeMillis();
            // try {
            //     HttpRequest request = createRequestForRandomOrder(ticker);
            //     // HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //     // if(response.statusCode() == 201){
            //     //     System.out.println("Sucessfully placed order: " +request);
            //     // }
            // // } catch (IOException | InterruptedException e) {
            // //     System.out.println("Http request experienced an error");
            // //     e.printStackTrace();
            // }
            HttpRequest request = createRequestForRandomOrder(ticker);
            System.out.println(request.toString());
            long end = System.currentTimeMillis();
            System.out.printf("LiquidityEngine : Order processed in %d milliseconds.", (end-start));
            Thread.sleep((60*1000)/frequency);
    }

    // public void startOrderStream() {
    //     if (running.get()) return; // Already running
    
    //     running.set(true);
    //     orderThread = new Thread(() -> {
    //         try {
    //             while (running.get()) {
    //                 postOrder();
    //             }
    //         } catch (InterruptedException e) {
    //             Thread.currentThread().interrupt(); // Restore interrupted status
    //             System.out.println("Order stream interrupted.");
    //         }
    //     });
    
    //     orderThread.start();
    // }

    // public void stopOrderStream() {
    //     running.set(false);
    //     if (orderThread != null) {
    //         orderThread.interrupt();
    //         try {
    //             orderThread.join(); // Wait for thread to finish
    //         } catch (InterruptedException e) {
    //             Thread.currentThread().interrupt();
    //             System.out.println("Interrupted while stopping the order stream.");
    //         }
    //     }
    // }
}