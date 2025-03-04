package orderMatchingEngineTest;

import static org.junit.jupiter.api.Assertions.*;

import orderMatchingEngine.Order;
import orderMatchingEngine.SellOrderComparator;
import org.junit.jupiter.api.Test;

class BuyOrderComparatorTest {

  private final SellOrderComparator comparator = new SellOrderComparator();

  @Test
  void testHigherPriceComesFirst() {
    Order order1 = new Order(Order.Type.SELL, Order.Ticker.A, 100.0, 2000); // Lower price
    Order order2 = new Order(Order.Type.SELL, Order.Ticker.A, 105.0, 1000); // Higher price

    assertTrue(
      comparator.compare(order1, order2) < 0,
      "Lower price should come later"
    );
  }

  @Test
  void testSamePrice_EarlierTimestampComesFirst() {
    Order order1 = new Order(Order.Type.SELL, Order.Ticker.A, 100.0, 2000); // Earlier timestamp
    Order order2 = new Order(Order.Type.SELL, Order.Ticker.A, 100.0, 1000); // Later timestamp

    assertTrue(
      comparator.compare(order1, order2) < 0,
      "Earlier timestamp should come first"
    );
  }

  @Test
  void testSamePriceAndTimestamp_AreEqual() {
    long fixedTimestamp = 1000000000L;
    Order order1 = new Order(
      Order.Type.SELL,
      Order.Ticker.A,
      100.0,
      1000,
      fixedTimestamp
    );
    Order order2 = new Order(
      Order.Type.SELL,
      Order.Ticker.A,
      100.0,
      1000,
      fixedTimestamp
    );

    assertEquals(
      0,
      comparator.compare(order1, order2),
      "Orders with same price and timestamp should be equal"
    );
  }
}
