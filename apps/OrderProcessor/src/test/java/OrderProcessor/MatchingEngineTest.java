package OrderProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
  void noMatchWhenPricesDiffer() {
    Order buyOrder = new Order(OrderType.BUY, Ticker.A, 100.0, 50);
    Order sellOrder = new Order(OrderType.SELL, Ticker.A, 110.0, 50);

    ArrayList<Document> result = processor.processOrder(buyOrder);
    assertEquals(0, result.size());

    result = processor.processOrder(sellOrder);
    assertEquals(0, result.size());
  }

  @Test
  void noMatchWhenEmptyBuyQueue() {
    Order order = new Order(OrderType.SELL, Ticker.A, 100.0, 50);
    assertEquals(0, processor.processOrder(order).size());
  }

  @Test
  void fullMatchRemovesOrders() {
    Order sellOrder = new Order(OrderType.SELL, Ticker.A, 100.0, 50);
    Order buyOrder = new Order(OrderType.BUY, Ticker.A, 100.0, 50);

    assertEquals(0, processor.processOrder(buyOrder).size());
    assertEquals(2, processor.processOrder(sellOrder).size());
  }

  @Test
  void partialFill() {
    Order sellOrder = new Order(OrderType.SELL, Ticker.A, 100.0, 50);
    Order buyOrder = new Order(OrderType.BUY, Ticker.A, 100.0, 30);

    processor.processOrder(buyOrder);
    assertEquals(2, processor.processOrder(sellOrder).size());

    Order partiallyFilledOrder = processor
      .getTradeBook(Ticker.A)
      .getSellOrders()
      .peek();

    assertNotNull(partiallyFilledOrder);
    assertEquals(20, partiallyFilledOrder.getQuantity());
  }
}
