package com.setap.tradingplatformapi.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;

import orderProcessor.Order;
import orderProcessor.OrderProcessor;
import orderProcessor.OrderType;
import orderProcessor.Ticker;
import org.bson.Document;

public class DatabaseUtils {


    public DatabaseUtils() {
    }

    public static Order createOrderAndInsertIntoDatabase(JsonObject body, MongoCollection<Document> ordersCollection) {
        String typeStr = body.getString("type");
        String tickerStr = body.getString("ticker");
        double price = body.getDouble("price");
        int quantity = body.getInteger("quantity");
        String userId = body.getString("userId");

        OrderType type = OrderType.valueOf(typeStr);
        Ticker ticker = Ticker.valueOf(tickerStr);

        Order order = new Order(type, userId, ticker, price, quantity);
        Document orderDoc = order.toDoc();

        ordersCollection.insertOne(orderDoc);

        return order;
    }

    public static ArrayList<Document> processOrderAndParseMatchesFound(Order order, OrderProcessor orderprocessor) {
        ArrayList<String> matchesFound = orderprocessor.processOrder(order);
        ArrayList<Document> matchesFoundAsMongoDBDocs = new ArrayList<>();
        for (String match : matchesFound) {
            Document doc = Document.parse(match);
            matchesFoundAsMongoDBDocs.add(doc);
        }
        return matchesFoundAsMongoDBDocs;
    }

    public static void updateDBToReflectFulfilledOrders(
            ArrayList<Document> matchesFoundAsMongoDBDocs,
            MongoCollection<Document> ordersCollection,
            MongoCollection<Document> usersCollection
    ) {
        Document buyOrder = matchesFoundAsMongoDBDocs.get(0);
        Document sellOrder = matchesFoundAsMongoDBDocs.get(1);

        boolean buyOrderFilled = buyOrder.getBoolean("filled");
        boolean sellOrderFilled = sellOrder.getBoolean("filled");
        String buyOrderId = buyOrder.getString("orderID");
        String sellOrderId = sellOrder.getString("orderID");
        String buyUserId = buyOrder.getString("userId");
        String sellUserId = sellOrder.getString("userId");

        if (buyOrderFilled) {
            int quantityChange = (int) (buyOrder.getInteger("quantityChange") * buyOrder.getDouble("price"));

            ordersCollection.updateOne(
                    Filters.eq("orderId", buyOrderId),
                    new Document("$set", new Document("filled", true))
            );

            usersCollection.updateOne(
                    Filters.eq("userId", buyUserId),
                    new Document("$inc", new Document("balance", -quantityChange))
            );
        }

        if (!buyOrderFilled) {
            //TODO decrement quantity by amount traded (?)
            //create orderHistory collection, send orders there when they are fulfilled, 
            //if we decrement quantity on the db, we mess with user history

            //                int quantityChange = buyOrder.getInteger("quantityChange");
            //
            //                ordersCollection.findOneAndUpdate(
            //                        Filters.eq("orderId", buyOrderId),
            //                        new Document("$inc", new Document("quantity", quantityChange))
            //                );
        }

        if (sellOrderFilled) {
            int quantityChange = (int) (sellOrder.getInteger("quantityChange") * sellOrder.getDouble("price"));

            ordersCollection.updateOne(
                    Filters.eq("orderId", sellOrderId),
                    new Document("$set", new Document("filled", true))
            );

            usersCollection.updateOne(
                    Filters.eq("userId", sellUserId),
                    new Document("$inc", new Document("balance", quantityChange))
            );
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
