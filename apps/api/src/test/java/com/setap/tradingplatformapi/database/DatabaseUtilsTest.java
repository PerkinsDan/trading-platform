package com.setap.tradingplatformapi.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import orderProcessor.*;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseUtilsTest {

    private OrderProcessor orderProcessor;

    @Mock
    MongoCollection<Document> mockOrdersCollection;

    @Mock
    MongoCollection<Document> mockUsersCollection;

    @Mock
    OrderProcessor mockOrderProcessor;

    @BeforeEach
    void setup() {
        mockOrdersCollection = mock(MongoCollection.class);
        mockUsersCollection = mock(MongoCollection.class);
        mockOrderProcessor = mock(OrderProcessor.class);
    }

    @Test
    void testProcessOrderAndParseMatchesFound() {
        Order order = new Order(OrderType.BUY, "userA", Ticker.A, 50.0, 10);

        List<String> fakeMatchesToBeProcessed = List.of(
                "{\"orderId\":\"12345\",\"userId\":\"userA\",\"price\":50.0,\"quantityChange\":10,\"filled\":true}",
                "{\"orderId\":\"67890\",\"userId\":\"userB\",\"price\":50.0,\"quantityChange\":10,\"filled\":true}"
        );

        when(mockOrderProcessor.processOrder(any(Order.class))).thenReturn(new ArrayList<>(fakeMatchesToBeProcessed));

        ArrayList<Document> matchesFound = DatabaseUtils.processOrderAndParseMatchesFound(order, mockOrderProcessor);

        ArrayList<Document> expectedMatches = new ArrayList<>();
        expectedMatches.add(Document.parse("{\"orderId\":\"12345\",\"userId\":\"userA\",\"price\":50.0,\"quantityChange\":10,\"filled\":true}"));
        expectedMatches.add(Document.parse("{\"orderId\":\"67890\",\"userId\":\"userB\",\"price\":50.0,\"quantityChange\":10,\"filled\":true}"));

        assertEquals(expectedMatches, matchesFound);
    }

    @Test
    void testUpdateDBToReflectFulfilledOrders(){

        Document buyOrderDoc = Document.parse("{\"orderId\":\"12345\",\"userId\":\"userA\",\"price\":50.0,\"quantityChange\":10,\"filled\":true}");
        Document sellOrderDoc = Document.parse("{\"orderId\":\"67890\",\"userId\":\"userB\",\"price\":50.0,\"quantityChange\":10,\"filled\":true}");

        ArrayList<Document> matchesFoundAsMongoDBDocs = new ArrayList<>(
                Arrays.asList(buyOrderDoc, sellOrderDoc)
        );

        DatabaseUtils.updateDBToReflectFulfilledOrders(
                matchesFoundAsMongoDBDocs,
                mockOrdersCollection,
                mockUsersCollection
        );

        verify(mockOrdersCollection).updateOne(
                eq(Filters.eq("orderId", "12345")),
                eq(new Document("$set", new Document("filled", true)))
        );

        verify(mockUsersCollection).updateOne(
                eq(Filters.eq("userId", "userA")),
                eq(new Document("$inc", new Document("balance", -500)))
        );

        verify(mockOrdersCollection).updateOne(
                eq(Filters.eq("orderId", "67890")),
                eq(new Document("$set", new Document("filled", true)))
        );

        verify(mockUsersCollection).updateOne(
                eq(Filters.eq("userId", "userB")),
                eq(new Document("$inc", new Document("balance", 500)))
        );
    }
}
