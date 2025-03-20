package org.database;

import com.mongodb.client.model.Filters;
import io.vertx.core.json.JsonObject;
import orderProcessor.Order;
import orderProcessor.OrderProcessor;
import orderProcessor.OrderType;
import orderProcessor.Ticker;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.print.Doc;
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
        ArrayList<String> matchesFound = orderprocessor.processOrder(order);
        ArrayList<Document> matchesFoundAsMongoDBDocs = new ArrayList<>();
        for (String match : matchesFound) {
            Document doc = Document.parse(match);
            matchesFoundAsMongoDBDocs.add(doc);
        }

        if (!matchesFound.isEmpty()) {
            Document buyOrder = matchesFoundAsMongoDBDocs.get(0);
            Document sellOrder = matchesFoundAsMongoDBDocs.get(1);

            boolean buyOrderFilled = buyOrder.getBoolean("filled");
            boolean sellOrderFilled = sellOrder.getBoolean("filled");

            if(buyOrderFilled) {
                //get the Order with the buyOrderID that we are passed
                //set that order's filled status to filled
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
