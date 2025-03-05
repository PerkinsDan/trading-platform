package orderMatchingEngineTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.PriorityQueue;
import orderMatchingEngine.MatchingEngine;
import orderMatchingEngine.Order;
import orderMatchingEngine.OrderProcessor;
import orderMatchingEngine.Ticker;
import orderMatchingEngine.OrderType;
import orderMatchingEngine.TradeBook;
import org.junit.jupiter.api.*;


public class MatchingEngineTest {

  private MatchingEngine engine;
  private OrderProcessor processor;

  @BeforeEach
  void setUp() {
    engine = MatchingEngine.getInstance();
    processor = new OrderProcessor();
  }

  @AfterEach
  void reset() {
    engine.resetInstance();
  }

  @Test
  void test_match_nothingToMatch_pricesDiffer() {
    //Add order to book of equity A, prices dont match so there should be no matching
    Order buyOrder = new Order(OrderType.BUY, Ticker.A, 100.0, 50);
    Order sellOrder = new Order(OrderType.SELL, Ticker.A, 110.0, 50);

    processor.addOrder(buyOrder);
    processor.addOrder(sellOrder);

    assertEquals(engine.match(MatchingEngine.getTradeBook(Ticker.A)), 0); //returning 0 means no matches
  }

  @Test
  void test_match_nothingToMatch_emptyBuyQueue() {
    //Add order to tradebook for A and no sell orders
    Order order = new Order(OrderType.SELL, Ticker.A, 100.0, 50);

    processor.addOrder(order);

    assertEquals(engine.match(MatchingEngine.getTradeBook(Ticker.A)), 0);
  }

  @Test
  void test_match_fullMatch_checkmatchAndEmptyBooksAfter() {
    Order sellOrder = new Order(OrderType.SELL, Ticker.A, 100.0, 50);
    Order buyOrder = new Order(OrderType.BUY, Ticker.A, 100.0, 50);

    processor.addOrder(buyOrder);
    processor.addOrder(sellOrder);

    TradeBook booker = MatchingEngine.getTradeBook(Ticker.A);
    int numTrades = engine.match(booker);

    assertEquals(numTrades, 1);
    //both orders should have been cleared of the books, so no matches should be made
    assertEquals(engine.match(MatchingEngine.getTradeBook(Ticker.A)), 0);
  }

  @Test
  void test_match_checkPartialFill() {
    Order sellOrder = new Order(OrderType.SELL, Ticker.A, 100.0, 50);
    Order buyOrder = new Order(OrderType.BUY, Ticker.A, 100.0, 30);

    processor.addOrder(buyOrder);
    processor.addOrder(sellOrder);

    //assert a match took place
    assertEquals(engine.match(MatchingEngine.getTradeBook(Ticker.A)), 1);

    //assert that part of the original sell order remains unfilled
    Order partiallyFilledOrder = MatchingEngine.getTradeBook(Ticker.A)
      .getSellBook()
      .peek();
    assertEquals(partiallyFilledOrder.getQuantity(), 20);
  }

  @Test
  void test_match_checkMultipleMatches() {
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

    assertEquals(5, engine.match(MatchingEngine.getTradeBook(Ticker.A)));
    //check that theres just one unfilled order left in buyBook()
    TradeBook book = MatchingEngine.getTradeBook(Ticker.A);
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
