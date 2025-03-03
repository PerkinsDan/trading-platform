package orderMatchingEngineTest;

import org.junit.jupiter.api.*;
import java.util.concurrent.*;
import orderMatchingEngine.OrderProcessor;
import orderMatchingEngine.Order;

import static org.junit.jupiter.api.Assertions.*;

class OrderProcessorTest {

    private OrderProcessor orderProcessor;

    @BeforeEach
    void setUp() {
        orderProcessor = new OrderProcessor();
    }

    @AfterEach
    void tearDown() {
        orderProcessor.shutdown();
    }

    @Test
    void testSubmitOrder_AddsOrderToQueue() {
        Order order = new Order(null, 0, 0);
        orderProcessor.submitOrder(order);

        PriorityBlockingQueue<Order> queue = OrderProcessor.getOrderQueue();
        assertTrue(queue.contains(order), "Order should be in the queue");
    }

    @Test
    void testOrdersProcessedInTimeOrder() throws InterruptedException {
        Order oldOrder = new Order(Order.Type.BUY, 0.0, 0);  // Simulate old order with timestamp 1000
        Order newOrder = new Order(Order.Type.BUY, 0.0,0);  // Simulate newer order with timestamp 2000

        orderProcessor.submitOrder(newOrder);
        orderProcessor.submitOrder(oldOrder);

        PriorityBlockingQueue<Order> queue = OrderProcessor.getOrderQueue();

        assertEquals(oldOrder, queue.poll(), "Older order should be processed first");
        assertEquals(newOrder, queue.poll(), "Newer order should be processed second");
    }

    @Test
    void testShutdown_TerminatesExecutor() {
        orderProcessor.shutdown();
        assertTrue(orderProcessor.isOrderProcessorShutDown(), "Executor should be shut down");
    }
}

