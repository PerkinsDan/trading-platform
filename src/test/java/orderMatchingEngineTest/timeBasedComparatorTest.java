package orderMatchingEngineTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import orderMatchingEngine.SellOrderComparator;
import orderMatchingEngine.Order;

class timeBasedComparatorTest {

    private final SellOrderComparator comparator = new SellOrderComparator();

    @Test
    void testLowerTimeStampComesFirst() {
        Order order1 = new Order(Order.Type.SELL,100.0, 1000, 100000L);
        Order order2 = new Order(Order.Type.SELL,105.0, 1000, 99999L); 

        assertTrue(comparator.compare(order1, order2) < 0, "Lower time stamp should come first");
        assertTrue(comparator.compare(order2, order1) > 0, "Bigger time stamp should come later");
    }



    @Test
    void testSamePriceAndTimestamp_AreEqual() {
        long fixedTimestamp = 1000000000L;
        Order order1 = new Order(Order.Type.SELL,100.0, 1000, fixedTimestamp);
        Order order2 = new Order(Order.Type.SELL,100.0, 1000, fixedTimestamp);

        assertEquals(0, comparator.compare(order1, order2), "Orders with same price and timestamp should be equal");
    }
}
