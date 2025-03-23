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

    public DatabaseUtils() {
    }

    public static Order createOrderAndInsertIntoDatabase(JsonObject body) {

        String typeStr = body.getString("type");
        String tickerStr = body.getString("ticker");
        double price = body.getDouble("price");
        int quantity = body.getInteger("quantity");
        String UserID = body.getString("UserID");

        OrderType type = OrderType.valueOf(typeStr);
        Ticker ticker = Ticker.valueOf(tickerStr);

        Order order = new Order(type, ticker, price, quantity, UserID);
        Document orderDoc = order.toDoc();

        var ordersCollection = MongoClientConnection.getCollection("orders");
        ordersCollection.insertOne(orderDoc);

        return order;
    }

    public static void processOrder(Order order) {
        var ordersCollection = MongoClientConnection.getCollection("orders");
        var usersCollection = MongoClientConnection.getCollection("users");
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
            String buyOrderId = buyOrder.getString("orderId");
            String sellOrderId = sellOrder.getString("orderId");

            if (buyOrderFilled) {

                int quantityChange = buyOrder.getInteger("quantity") * buyOrder.getInteger("price");

                ordersCollection.updateOne(
                        Filters.eq("orderId", buyOrderId),
                        new Document("$set", new Document("filled", true)));

                usersCollection.updateOne(
                        Filters.eq("orderId", buyOrderId),
                        new Document("$inc", new Document("balance", -quantityChange)));
            }

            if (!buyOrderFilled) {
                //TODO decrement quantity by amount traded (?)
                //if we decrement quantity on the db, we mess with user history

//                int quantityChange = buyOrder.getInteger("quantityChange");
//
//                ordersCollection.findOneAndUpdate(
//                        Filters.eq("orderId", buyOrderId),
//                        new Document("$inc", new Document("quantity", quantityChange))
//                );
            }

            if (sellOrderFilled) {

                int quantityChange = sellOrder.getInteger("quantity") * sellOrder.getInteger("price");

                ordersCollection.updateOne(
                        Filters.eq("orderId", sellOrderId),
                        new Document("$set", new Document("filled", true)));

                usersCollection.updateOne(
                        Filters.eq("orderId", sellOrderId),
                        new Document("$inc", new Document("balance", quantityChange)));

            }

            if (!sellOrderFilled) {
                //TODO decrement quantity by amount traded (?)
                //if we decrement quantity on the db, we mess with user history



//                int quantityChange = sellOrder.getInteger("quantityChange");
//
//                ordersCollection.findOneAndUpdate(
//                        Filters.eq("orderId", sellOrderId),
//                        new Document("$inc", new Document("quantity", -quantityChange))
//                );
            }
        }
    }
}
