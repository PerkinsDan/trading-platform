package orderMatchingEngine;

public class OrderProcessor{

    public OrderProcessor(){}

    public void addOrder(Order order){
        TradeBook book = MatchingEngine.getTradeBook(order.getTicker());
        book.addToBook(order);
    }

    public void match(MatchingEngine matchingEngine,Order order){
        matchingEngine.match(MatchingEngine.getTradeBook(order.getTicker()));
    }

}