package com.tradingplatform.LiquidityEngine;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.tradingplatform.orderprocessor.orders.Ticker;

import io.github.cdimascio.dotenv.Dotenv;

public class OrderPoster{

    private static final String JSON_ERROR = "Error while converting to JSON";
    private static final String MARKET_DATA_ERROR = "Error while retrieving market price from MarketData.";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Random random = new Random();
    Dotenv dotenv = Dotenv.configure().load();

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

        String request = generator.generateMockOrderAsJson(ticker);

        if (request.equals(JSON_ERROR) || request.equals(MARKET_DATA_ERROR)) {
            System.out.println(LocalDateTime.now().format(formatter) + ": Failed to generate order for ticker " + ticker +
                               " — Reason: " + request);
            return null;
        }

        return HttpRequest.newBuilder()
            .uri(URI.create(dotenv.get("BASE_URL_DEV") + "orders/create"))
            .POST(HttpRequest.BodyPublishers.ofString(generator.generateMockOrderAsJson(ticker)))
            .build();      
    }

    public void postOrder() throws InterruptedException{
            String ticker = randomTicker().toString();
            client = HttpClient.newHttpClient();
            long start = System.currentTimeMillis();
            HttpRequest request = createRequestForRandomOrder(ticker);

            if (request == null) {
                System.out.println(LocalDateTime.now().format(formatter) + ": Skipping order submission due to bad request. See reason above");
                return;
            }

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if(response.statusCode() == 201){
                    System.out.println(LocalDateTime.now().format(formatter) + ": Sucessfully placed order: " + request);
                }
            } catch (IOException | InterruptedException e) {
                System.out.println(LocalDateTime.now().format(formatter) + ": Http request experienced an error while submitting order");
            }
            long end = System.currentTimeMillis();
            System.out.println(request.toString());
            System.out.printf(LocalDateTime.now() + ": LiquidityEngine : Order processed in %d milliseconds.", (end-start));
            Thread.sleep((60*1000)/frequency);
    }
}