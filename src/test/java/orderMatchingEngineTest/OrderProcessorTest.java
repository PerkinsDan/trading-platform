package orderMatchingEngineTest;

import org.junit.jupiter.api.*;

import java.util.PriorityQueue;
import orderMatchingEngine.OrderProcessor;
import orderMatchingEngine.TradeBook;
import orderMatchingEngine.Order;
import orderMatchingEngine.MatchingEngine;

import static org.junit.jupiter.api.Assertions.*;

class OrderProcessorTest {

    private OrderProcessor orderProcessor;

    @BeforeEach
    void setUp() {
        MatchingEngine engine = new MatchingEngine();
        orderProcessor = new OrderProcessor();
    }


    @Test
    void testSubmitOrder_AddsOrderToBook() {
        Order order = new Order(Order.Type.SELL,Order.Ticker.A,100.0, 2000);
        Order order1 = new Order(Order.Type.BUY,Order.Ticker.A,100.0, 2000);
        orderProcessor.addOrder(order);
        orderProcessor.addOrder(order1);
        TradeBook book = MatchingEngine.getTradeBook(Order.Ticker.A);
        PriorityQueue<Order> buyBook = book.getBuyBook();
        PriorityQueue<Order> sellBook = book.getSellBook();
        assertTrue(sellBook.contains(order));
        assertTrue(buyBook.contains(order1));
    }

    @Test
    void testOrdersProcessedInTimeOrder(){
        //two sell orders created with same price but different time stamps
        Order oldSellOrder = new Order(Order.Type.SELL,Order.Ticker.A,100.0, 2000);  
        Order newSellOrder = new Order(Order.Type.SELL,Order.Ticker.A,100.0, 2000);  

        Order oldBuyOrder = new Order(Order.Type.BUY,Order.Ticker.A,100.0, 2000);  
        Order newBuyOrder = new Order(Order.Type.BUY,Order.Ticker.A,100.0, 2000);  


        orderProcessor.addOrder(newSellOrder);
        orderProcessor.addOrder(oldSellOrder);
        orderProcessor.addOrder(newBuyOrder);
        orderProcessor.addOrder(oldBuyOrder);

        TradeBook book = MatchingEngine.getTradeBook(Order.Ticker.A);
        PriorityQueue<Order> sellQueue = book.getSellBook();
        PriorityQueue<Order> buyQueue = book.getBuyBook();

        assertEquals(oldSellOrder, sellQueue.poll(), "Older order should be processed first");
        assertEquals(newSellOrder, sellQueue.poll(), "Newer order should be processed second");
        assertEquals(oldBuyOrder, buyQueue.poll(), "Older order should be processed first");
        assertEquals(newBuyOrder, buyQueue.poll(), "Newer order should be processed second");
    }

    @Test
    void testAddOrder_pollInOrderOfPrice(){
        long fixedTimestamp = 1000000L;
        Order cheapSellOrder = new Order(Order.Type.SELL,Order.Ticker.A,95.0, 2000,fixedTimestamp );  
        Order expensiveSellOrder = new Order(Order.Type.SELL,Order.Ticker.A,105.0, 2000,fixedTimestamp);  

        Order cheapBuyOrder = new Order(Order.Type.BUY,Order.Ticker.A,95.0, 2000,fixedTimestamp);  
        Order expensiveBuyOrder = new Order(Order.Type.BUY,Order.Ticker.A,105.0, 2000,fixedTimestamp);

        orderProcessor.addOrder(expensiveBuyOrder);
        orderProcessor.addOrder(cheapBuyOrder);
        orderProcessor.addOrder(cheapSellOrder);
        orderProcessor.addOrder(expensiveSellOrder);

        TradeBook book = MatchingEngine.getTradeBook(Order.Ticker.A);
        PriorityQueue<Order> buyBook = book.getBuyBook();
        PriorityQueue<Order> sellBook = book.getSellBook();
 
        assertEquals(expensiveBuyOrder, buyBook.poll());
        assertEquals(cheapBuyOrder, buyBook.poll());
        assertEquals(cheapSellOrder, sellBook.poll());
        assertEquals(expensiveSellOrder, sellBook.poll());

    }
}

