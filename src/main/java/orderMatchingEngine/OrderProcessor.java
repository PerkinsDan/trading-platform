package orderMatchingEngine;

public class OrderProcessor{

    public OrderProcessor(){}

    public void addOrder(Order order){
        TradeBook book = MatchingEngine.getTradeBook(order.getTicker().name());
        book.addToBook(order);
    }

    public static void match(Order order){
        MatchingEngine.match(MatchingEngine.getTradeBook(order.getTicker().name()));
    }

}