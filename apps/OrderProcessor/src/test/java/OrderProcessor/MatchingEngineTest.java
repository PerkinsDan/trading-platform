package OrderProcessor;


import static org.junit.jupiter.api.Assertions.*;

import java.util.PriorityQueue;

import org.junit.jupiter.api.*;

public class MatchingEngineTest {

  private OrderProcessor processor;

  @BeforeEach
  void setUp() {
    processor = OrderProcessor.getInstance();
  }

  @AfterEach
  void reset() {
    processor.resetInstance();
  }

  @Test
  void testWithNoPossibleMatches() {
    //Add order to book of equity A, prices dont match so there should be no matching
    Order buyOrder = new Order(OrderType.BUY, Ticker.A, 100.0, 50);
    Order sellOrder = new Order(OrderType.SELL, Ticker.A, 110.0, 50);

    processor.addOrder(buyOrder);
    processor.addOrder(sellOrder);

    assertEquals(processor.MatchTrades(sellOrder).size(), 0); 
  }

  @Test
  void testNoMatchesFromEmptyQueues() {
    //Add order to tradebook for A and no sell orders, verify no matches occur
    Order order = new Order(OrderType.SELL, Ticker.A, 100.0, 50);

    processor.addOrder(order);

    assertEquals(processor.MatchTrades(order).size(), 0);
  }

  @Test
  void testMatchAndEmptyBooksAfter() {
    //Create two orer that will match and leave the trade book empty. Verify that the book is empty after
    Order sellOrder = new Order(OrderType.SELL, Ticker.A, 100.0, 50);
    Order buyOrder = new Order(OrderType.BUY, Ticker.A, 100.0, 50);

    processor.addOrder(buyOrder);
    processor.addOrder(sellOrder);

    int numTrades = processor.MatchTrades(buyOrder).size();

    assertEquals(numTrades, 2);
    assertEquals(processor.MatchTrades(buyOrder).size(), 0);
  }

  @Test
  void testPartialFill() {
    Order sellOrder = new Order(OrderType.SELL, Ticker.A, 100.0, 50);
    Order buyOrder = new Order(OrderType.BUY, Ticker.A, 100.0, 30);

    processor.addOrder(buyOrder);
    processor.addOrder(sellOrder);

    //assert a match took place
    assertEquals(processor.MatchTrades(sellOrder).size(), 2);

    //assert that part of the original sell order remains unfilled
    Order partiallyFilledOrder = OrderProcessor.getTradeBook(Ticker.A)
      .getSellBook()
      .peek();
    assertEquals(partiallyFilledOrder.getQuantity(), 20);
  }

  @Test
  void testMultipleMatches() {
    Order[] orders = {
      new Order(OrderType.SELL, Ticker.A, 99.0, 575),
      new Order(OrderType.SELL, Ticker.A, 100.0, 500),
      new Order(OrderType.SELL, Ticker.A, 100.0, 50),
      new Order(OrderType.SELL, Ticker.A, 101.5, 150),
      new Order(OrderType.BUY, Ticker.A, 98.0, 30),
      new Order(OrderType.BUY, Ticker.A, 100.0, 700),
      new Order(OrderType.BUY, Ticker.A, 100.0, 300),
      new Order(OrderType.BUY, Ticker.A, 102.0, 130),
    };

    for (Order order : orders) {
      processor.addOrder(order);
    }
    
    Order dummyOrder = new Order(OrderType.BUY, Ticker.A, 102.0, 130);

    assertEquals(10, processor.MatchTrades(dummyOrder).size());
    
    //check that theres just one unfilled order left in buyBook()
    TradeBook book = OrderProcessor.getTradeBook(Ticker.A);
    PriorityQueue<Order> buyBook = book.getBuyBook();
    PriorityQueue<Order> sellBook = book.getSellBook();

    //check sellBook has 1 unfilled trade and check quantity
    assertTrue(sellBook.size() == 1);
    Order remaining = sellBook.peek();
    assertEquals(150, remaining.getQuantity());
    assertEquals(101.5, remaining.getPrice());

    //check 2 items remain in buyBook
    assertEquals(2, buyBook.size());

    //check top item
    Order buyresidual = buyBook.poll();
    assertTrue(buyresidual.getPrice() == 100.0);
    assertTrue(buyresidual.getQuantity() == 5);

    //check second item
    buyresidual = buyBook.poll();
    assertTrue(buyresidual.getPrice() == 98.0);
    assertTrue(buyresidual.getQuantity() == 30);
  }
}
