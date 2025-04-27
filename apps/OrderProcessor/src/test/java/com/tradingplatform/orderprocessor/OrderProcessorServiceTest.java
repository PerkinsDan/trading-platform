package com.tradingplatform.orderprocessor;

import static org.junit.jupiter.api.Assertions.*;

import com.tradingplatform.orderprocessor.orders.*;
import com.tradingplatform.orderprocessor.orders.Order;
import java.util.PriorityQueue;
import org.junit.jupiter.api.*;

class OrderProcessorServiceTest {

  private OrderProcessorService orderProcessor;

  @BeforeEach
  void setUp() {
    orderProcessor = OrderProcessorService.getInstance();
  }

  @AfterEach
  void reset() {
    orderProcessor.resetInstance();
  }

  @Test
  void testOrderCancellation() {
    com.tradingplatform.orderprocessor.orders.Order order =
      new com.tradingplatform.orderprocessor.orders.Order(
        OrderType.SELL,
        "dummyID",
        Ticker.AAPL,
        110.0,
        50
      );
    orderProcessor.processOrder(order);
    Boolean result = orderProcessor.cancelOrder(
      order.getId().toString(),
      order.getTicker().name(),
      order.getType().name()
    );
    assertTrue(result);
  }

  @Test
  void addBuyOrder() {
    com.tradingplatform.orderprocessor.orders.Order order =
      new com.tradingplatform.orderprocessor.orders.Order(
        OrderType.BUY,
        "dummyID",
        Ticker.AAPL,
        100.0,
        2000
      );
    orderProcessor.processOrder(order);

    TradeBook book = orderProcessor.getTradeBook(Ticker.AAPL);

    PriorityQueue<com.tradingplatform.orderprocessor.orders.Order> buyBook =
      book.getBuyOrders();
    assertTrue(buyBook.contains(order));
  }

  @Test
  void addSellOrder() {
    com.tradingplatform.orderprocessor.orders.Order order =
      new com.tradingplatform.orderprocessor.orders.Order(
        OrderType.SELL,
        "dummyID",
        Ticker.AAPL,
        100.0,
        2000
      );
    orderProcessor.processOrder(order);

    TradeBook book = orderProcessor.getTradeBook(Ticker.AAPL);

    PriorityQueue<com.tradingplatform.orderprocessor.orders.Order> sellBook =
      book.getSellOrders();
    assertTrue(sellBook.contains(order));
  }

  @Test
  void inTimeOrder() {
    com.tradingplatform.orderprocessor.orders.Order oldBuyOrder =
      new com.tradingplatform.orderprocessor.orders.Order(
        OrderType.BUY,
        "dummyID",
        Ticker.AAPL,
        100.0,
        2
      );
    com.tradingplatform.orderprocessor.orders.Order newBuyOrder =
      new com.tradingplatform.orderprocessor.orders.Order(
        OrderType.BUY,
        "dummyID",
        Ticker.AAPL,
        100.0,
        2000
      );

    orderProcessor.processOrder(newBuyOrder);
    orderProcessor.processOrder(oldBuyOrder);

    TradeBook book = orderProcessor.getTradeBook(Ticker.AAPL);
    PriorityQueue<com.tradingplatform.orderprocessor.orders.Order> buyQueue =
      book.getBuyOrders();

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
    com.tradingplatform.orderprocessor.orders.Order cheapSellOrder =
      new com.tradingplatform.orderprocessor.orders.Order(
        OrderType.SELL,
        "dummyID",
        Ticker.AAPL,
        95.0,
        2000
      );
    com.tradingplatform.orderprocessor.orders.Order expensiveSellOrder =
      new com.tradingplatform.orderprocessor.orders.Order(
        OrderType.SELL,
        "dummyID",
        Ticker.AAPL,
        105.0,
        2000
      );

    orderProcessor.processOrder(cheapSellOrder);
    orderProcessor.processOrder(expensiveSellOrder);

    TradeBook book = orderProcessor.getTradeBook(Ticker.AAPL);
    PriorityQueue<com.tradingplatform.orderprocessor.orders.Order> sellBook =
      book.getSellOrders();

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
    com.tradingplatform.orderprocessor.orders.Order cheapBuyOrder =
      new com.tradingplatform.orderprocessor.orders.Order(
        OrderType.BUY,
        "dummyID",
        Ticker.AAPL,
        100.0,
        2000
      );
    com.tradingplatform.orderprocessor.orders.Order expensiveBuyOrder =
      new com.tradingplatform.orderprocessor.orders.Order(
        OrderType.BUY,
        "dummyID",
        Ticker.AAPL,
        105.0,
        2000
      );

    orderProcessor.processOrder(cheapBuyOrder);
    orderProcessor.processOrder(expensiveBuyOrder);

    TradeBook book = orderProcessor.getTradeBook(Ticker.AAPL);
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
