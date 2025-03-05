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

class OrderProcessorTest {

  private OrderProcessor orderProcessor;
  private MatchingEngine engine;

  @BeforeEach
  void setUp() {
    engine = MatchingEngine.getInstance();
    orderProcessor = new OrderProcessor();
  }

  @AfterEach
  void reset() {
    engine.resetInstance();
  }

  @Test
  void testSubmitOrder_AddsOrderToBook() {
    Order order = new Order(OrderType.SELL, Ticker.A, 100.0, 2000);
    Order order1 = new Order(OrderType.BUY, Ticker.A, 100.0, 2000);
    orderProcessor.addOrder(order);
    orderProcessor.addOrder(order1);
    TradeBook book = MatchingEngine.getTradeBook(Ticker.A);
    PriorityQueue<Order> buyBook = book.getBuyBook();
    PriorityQueue<Order> sellBook = book.getSellBook();
    assertTrue(sellBook.contains(order));
    assertTrue(buyBook.contains(order1));
  }

  @Test
  void testOrdersProcessedInTimeOrder() {
    //two sell orders created with same price but different time stamps
    Order oldSellOrder = new Order(
      OrderType.SELL,
      Ticker.A,
      100.0,
      2000
    );
    Order newSellOrder = new Order(
      OrderType.SELL,
      Ticker.A,
      100.0,
      2000
    );

    Order oldBuyOrder = new Order(OrderType.BUY, Ticker.A, 100.0, 2000);
    Order newBuyOrder = new Order(OrderType.BUY, Ticker.A, 100.0, 2000);

    orderProcessor.addOrder(newSellOrder);
    orderProcessor.addOrder(oldSellOrder);
    orderProcessor.addOrder(newBuyOrder);
    orderProcessor.addOrder(oldBuyOrder);

    TradeBook book = MatchingEngine.getTradeBook(Ticker.A);
    PriorityQueue<Order> sellQueue = book.getSellBook();
    PriorityQueue<Order> buyQueue = book.getBuyBook();

    assertEquals(
      oldSellOrder,
      sellQueue.poll(),
      "Older order should be processed first"
    );
    assertEquals(
      newSellOrder,
      sellQueue.poll(),
      "Newer order should be processed second"
    );
    assertEquals(
      oldBuyOrder,
      buyQueue.poll(),
      "Older order should be processed first"
    );
    assertEquals(
      newBuyOrder,
      buyQueue.poll(),
      "Newer order should be processed second"
    );
  }
  

  void testAddOrder_pollInOrderOfPrice() {

    Order cheapSellOrder = new Order(
    OrderType.SELL,
    Ticker.A,
    95.0,
    2000
  );
  cheapSellOrder.setTimeStamp(1000000L);
  Order expensiveSellOrder = new Order(
    OrderType.SELL,
    Ticker.A,
    105.0,
    2000
  );
  expensiveSellOrder.setTimeStamp(1000000L);

  orderProcessor.addOrder(cheapSellOrder);
  orderProcessor.addOrder(expensiveSellOrder);

  TradeBook book = MatchingEngine.getTradeBook(Ticker.A);
  PriorityQueue<Order> sellBook = book.getSellBook();

  assertEquals(cheapSellOrder, sellBook.poll());
  assertEquals(expensiveSellOrder, sellBook.poll());
  
    }
}

