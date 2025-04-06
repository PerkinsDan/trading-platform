package LoadTester;

import orderProcessor.*;

public class Main {
    public static void main(String[] args) {
        OrderProcessor orderProcessor = OrderProcessor.getInstance();
        MockOrderGenerator generator = new MockOrderGenerator(150.00, Ticker.A);

        while(true){
            orderProcessor.processOrder(generator.generateMockOrder());
        }


        
    }

}
