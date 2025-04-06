package LiquidityEngine;

import java.io.IOException;

import orderProcessor.Ticker;


public class LiquidityService {
    
    public LiquidityService(){
        for (Ticker ticker : Ticker.values()){
            final OrderPoster orderPoster = new OrderPoster(150,ticker,15);

            Thread orderPosterThread = new Thread(() -> {
                try {
                    System.out.println("Creating daemon liquidity thread for " + ticker);
                    orderPoster.startOrderStream();
                } catch (IOException | InterruptedException e) {
                    System.out.println("Error for " + ticker + " thread:" + e.getMessage());
                }
            });

            orderPosterThread.setDaemon(true);
            orderPosterThread.start();
        }
        System.out.println("Liquidity engine daemon threads created successfully!");

    }

}
