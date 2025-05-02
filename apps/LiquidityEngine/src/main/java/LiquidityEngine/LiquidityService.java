package LiquidityEngine;

import java.io.IOException;
import java.util.ArrayList;

import com.setap.marketdata.MarketDataService;

import orderProcessor.Ticker;


public class LiquidityService {
    
    public LiquidityService(){
        MarketDataService marketDataService = MarketDataService.getInstance();
        MockOrderGenerator generator = new MockOrderGenerator();
        
        Thread liquidityEngineThread = new Thread(() -> {
            while(true){
                g


            }

        });


    }

}
