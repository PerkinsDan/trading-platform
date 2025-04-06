package LoadTester;

import orderProcessor.Order;
import orderProcessor.OrderType;
import orderProcessor.Ticker;
import org.apache.commons.math3.distribution.NormalDistribution;
import java.util.Random;

public class MockOrderGenerator{

    private double price;
    private Ticker ticker;
    private NormalDistribution distribution;
    private final Random random = new Random();


    protected MockOrderGenerator(double price, Ticker ticker){
        this.price = price;
        this.ticker = ticker;
        distribution = new NormalDistribution(price,(0.3 * price));
    }

    private OrderType randomOrderType(){
        return (random.nextInt(2)%2 == 0) ? OrderType.BUY : OrderType.SELL;
      
    }

    private int randomQuantity(){
        return random.nextInt(5000)+1;
    }

    public Order generateMockOrder(){
        Order order  = new Order(randomOrderType(), ticker, distribution.sample(),randomQuantity(), "LoadTester");
        System.out.println("Mock order created:" + order.toString());
        return order;
    }


}