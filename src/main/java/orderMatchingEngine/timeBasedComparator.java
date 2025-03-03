package orderMatchingEngine;
import java.util.Comparator;

public class timeBasedComparator implements Comparator<Order> {
    @Override
    public int compare(Order order1, Order order2){
        return Long.compare(order1.getTimestamp(), order2.getTimestamp());
    }
    
}
