package LoadTester;

import orderProcessor.Ticker;

public class OrderPoster{

    private MockOrderGenerator generator;
    private int frequency; // number of orders submitted per minute
    private final String postUrl;

    public OrderPoster(double price, Ticker ticker, int frequency){
        this.frequency = frequency;
        generator = new MockOrderGenerator(price, ticker);
    }

    public void applyLoad(){
        //at given frequency, generate a mock order and send it to the order processor for ordering.

    }


    
}