package orderProcessor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.PriorityQueue;
import org.junit.jupiter.api.*;

class OrderProcessorTest {

  private OrderProcessor orderProcessor;

  @BeforeEach
  void setUp() {
    orderProcessor = OrderProcessor.getInstance();
  }

  @AfterEach
  void reset() {
    orderProcessor.resetInstance();
  }

  @Test
  void testOrderCancellation() {
    Order order = new Order(OrderType.SELL, Ticker.A, 110.0, 50, "dummyID");
    orderProcessor.processOrder(order);
    assertTrue(orderProcessor.cancelOrder(order));
  }

  @Test
  void addBuyOrder() {
    Order order = new Order(OrderType.BUY, Ticker.A, 100.0, 2000, "dummyID");
    orderProcessor.processOrder(order);

    TradeBook book = orderProcessor.getTradeBook(Ticker.A);

    PriorityQueue<Order> buyBook = book.getBuyOrders();
    assertTrue(buyBook.contains(order));
  }

  @Test
  void addSellOrder() {
    Order order = new Order(OrderType.SELL, Ticker.A, 100.0, 2000, "dummyID");
    orderProcessor.processOrder(order);

    TradeBook book = orderProcessor.getTradeBook(Ticker.A);

    PriorityQueue<Order> sellBook = book.getSellOrders();
    assertTrue(sellBook.contains(order));
  }

  @Test
  void inTimeOrder() {
    Order oldBuyOrder = new Order(OrderType.BUY, Ticker.A, 100.0, 2, "dummyID");
    Order newBuyOrder = new Order(
      OrderType.BUY,
      Ticker.A,
      100.0,
      2000,
      "dummyID"
    );

    orderProcessor.processOrder(newBuyOrder);
    orderProcessor.processOrder(oldBuyOrder);

    TradeBook book = orderProcessor.getTradeBook(Ticker.A);
    PriorityQueue<Order> buyQueue = book.getBuyOrders();

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

  @Test
  void sellByPriceDesc() {
    Order cheapSellOrder = new Order(
      OrderType.SELL,
      Ticker.A,
      95.0,
      2000,
      "dummyID"
    );
    Order expensiveSellOrder = new Order(
      OrderType.SELL,
      Ticker.A,
      105.0,
      2000,
      "dummyID"
    );

    orderProcessor.processOrder(cheapSellOrder);
    orderProcessor.processOrder(expensiveSellOrder);

    TradeBook book = orderProcessor.getTradeBook(Ticker.A);
    PriorityQueue<Order> sellBook = book.getSellOrders();

    assertEquals(
      cheapSellOrder,
      sellBook.poll(),
      "Cheap sell order should be processed first"
    );
    assertEquals(
      expensiveSellOrder,
      sellBook.poll(),
      "Expensive sell order should be processed second"
    );
  }

  @Test
  void buyByPriceAsc() {
    Order cheapBuyOrder = new Order(
      OrderType.BUY,
      Ticker.A,
      100.0,
      2000,
      "dummyID"
    );
    Order expensiveBuyOrder = new Order(
      OrderType.BUY,
      Ticker.A,
      105.0,
      2000,
      "dummyID"
    );

    orderProcessor.processOrder(cheapBuyOrder);
    orderProcessor.processOrder(expensiveBuyOrder);

    TradeBook book = orderProcessor.getTradeBook(Ticker.A);
    PriorityQueue<Order> buyQueue = book.getBuyOrders();

    assertEquals(
      expensiveBuyOrder,
      buyQueue.poll(),
      "Expensive sell order should be processed first"
    );
    assertEquals(
      cheapBuyOrder,
      buyQueue.poll(),
      "Cheap sell order should be processed second"
    );
  }
}
