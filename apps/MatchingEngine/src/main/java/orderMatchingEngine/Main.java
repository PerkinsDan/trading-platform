package orderMatchingEngine;

public class Main {

  public static void main(String[] args) {
    MatchingEngine matchingEngine = MatchingEngine.getInstance();
    OrderProcessor processor = new OrderProcessor();
    // TO-DO : This temporary until we have an API to function as an entry point to the MatchingEngine
    Order[] orders = {
      new Order(Order.Type.SELL, Order.Ticker.A, 99.0, 575),
      new Order(Order.Type.SELL, Order.Ticker.A, 100.0, 500),
      new Order(Order.Type.SELL, Order.Ticker.A, 100.0, 50),
      new Order(Order.Type.SELL, Order.Ticker.A, 101.5, 150),
      new Order(Order.Type.BUY, Order.Ticker.A, 98.0, 30),
      new Order(Order.Type.BUY, Order.Ticker.A, 100.0, 700),
      new Order(Order.Type.BUY, Order.Ticker.A, 100.0, 300),
      new Order(Order.Type.BUY, Order.Ticker.A, 102.0, 130),
    };

    for (Order order : orders) {
      processor.addOrder(order);
    }

    //Run matching engine
    long start = System.currentTimeMillis();
    int count = matchingEngine.match(
      MatchingEngine.getTradeBook(Order.Ticker.A)
    );
    long end = System.currentTimeMillis();
    long time = end - start;
    System.out.println(count + " trades matched in " + time + "ms");
    System.out.println("average of " + time / 5 + "ms /trade");
  }
}
