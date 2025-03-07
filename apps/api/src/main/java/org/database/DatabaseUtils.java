package org.database;

import com.mongodb.client.model.Filters;
import io.vertx.core.json.JsonObject;
import orderProcessor.Order;
import orderProcessor.OrderProcessor;
import orderProcessor.OrderType;
import orderProcessor.Ticker;
import org.bson.Document;

import java.util.ArrayList;

public class DatabaseUtils {

    static OrderProcessor orderprocessor = OrderProcessor.getInstance();

    public DatabaseUtils() {}

    public static Order createOrderAndInsertIntoDatabase(JsonObject body){

        String typeStr = body.getString("type");
        String tickerStr = body.getString("ticker");
        double price = body.getDouble("price");
        int quantity = body.getInteger("quantity");

        OrderType type = OrderType.valueOf(typeStr);
        Ticker ticker = Ticker.valueOf(tickerStr);

        Document newOrderDoc = new Document()
                .append("type", type)
                .append("ticker", ticker)
                .append("price", price)
                .append("quantity", quantity);

        var ordersCollection = MongoClientConnection.getCollection("orders");
        ordersCollection.insertOne(newOrderDoc);

        return new Order(type, ticker, price, quantity);
    }

    public static void processOrder(Order order){
        var ordersCollection = MongoClientConnection.getCollection("orders");
        ArrayList<Document> matchesFound = orderprocessor.processOrder(order);

        if (!matchesFound.isEmpty()) {
            Document buyOrder = matchesFound.get(0);
            Document sellOrder = matchesFound.get(1);

            boolean buyOrderFilled = buyOrder.getBoolean("filled");
            boolean sellOrderFilled = sellOrder.getBoolean("filled");

            if(buyOrderFilled) {
                ordersCollection.deleteOne(Filters.eq("orderId", buyOrder.getString("orderId")));
            }

            if(!buyOrderFilled){
                String orderId = buyOrder.getString("orderId");
                int quantityChange = buyOrder.getInteger("quantityChange");

                ordersCollection.findOneAndUpdate(
                        Filters.eq("orderId", orderId),
                        new Document("$inc", new Document("quantity", quantityChange))
                );
            }

            if(sellOrderFilled){
                ordersCollection.deleteOne(Filters.eq("orderId", sellOrder.getString("orderId")));
            }

            if(!sellOrderFilled){
                String orderId = sellOrder.getString("orderId");
                int quantityChange = sellOrder.getInteger("quantityChange");

                ordersCollection.findOneAndUpdate(
                        Filters.eq("orderId", orderId),
                        new Document("$inc", new Document("quantity", -quantityChange))
                );
            }
        }
    }
}
