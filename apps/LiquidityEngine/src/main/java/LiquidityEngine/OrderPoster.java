package LiquidityEngine;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import orderProcessor.Ticker;

public class OrderPoster{

    private MockOrderGenerator generator;
    private int frequency; // number of orders submitted per minute
    private HttpClient client;
    private final String postURL = "http://localhost:8080/create-order";

    public OrderPoster(double price, Ticker ticker, int frequency){
        this.frequency = frequency;
        this.generator = new MockOrderGenerator(price, ticker);
        client = HttpClient.newHttpClient();
        

    }

    private HttpRequest createRequestForRandomOrder(){   
        return HttpRequest.newBuilder()
            .uri(URI.create(postURL))
            .POST(HttpRequest.BodyPublishers.ofString(generator.generateMockOrder()))
            .build();      
    }

    public void startOrderStream() throws IOException, InterruptedException{
        // synronous streaming of randmon orders to simualte liquidity.
        HttpResponse<String> response = client.send(createRequestForRandomOrder(),HttpResponse.BodyHandlers.ofString());
        Boolean previousOrderSucessful = response.statusCode() == 201;
        
        if (previousOrderSucessful){
            System.out.println(" LiquidityEngine : Succesfully created order! Beginning order stream...");
            while (previousOrderSucessful){
                // send random orders as long the last one went sucessfully
                Thread.sleep((60/frequency)*1000);// set frequency of requests per minute, ie if frequency is 10, a request is sent every (60/10)*1000 = 6000 milliseconds or 6 seconds
                long start = System.currentTimeMillis();
                response = client.send(createRequestForRandomOrder(), HttpResponse.BodyHandlers.ofString());
                long end = System.currentTimeMillis();
                previousOrderSucessful = response.statusCode() == 201;
                System.out.printf("LiquidityEngine : Order processed in %d milliseconds.", (end-start));
            }
        } else {
            System.out.printf("Order submission failed - HTTPS Status : %d", response.statusCode());
        }
    }
}