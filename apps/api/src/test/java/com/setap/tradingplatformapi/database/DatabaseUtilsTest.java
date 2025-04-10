package com.setap.tradingplatformapi.database;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import orderProcessor.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class DatabaseUtilsTest {

  @Mock
  MongoCollection<Document> mockActiveOrdersCollection;

  @Mock
  MongoCollection<Document> mockUsersCollection;

  @Mock
  MongoCollection<Document> mockOrderHistoryCollection;

    @Mock
    OrderProcessor mockOrderProcessor;

    @BeforeEach
    void setup() {
        mockActiveOrdersCollection = mock(MongoCollection.class);
        mockUsersCollection = mock(MongoCollection.class);
        mockOrderHistoryCollection = mock(MongoCollection.class);
        mockOrderProcessor = mock(OrderProcessor.class);
    }

    @Test
    void testProcessOrderAndParseMatchesFound() {
        Order order = new Order(OrderType.BUY, "userA", Ticker.AAPL, 50.0, 10);

        List<String> fakeMatchesToBeProcessed = List.of(
                "{\"orderID\":\"12345\",\"userId\":\"userA\",\"price\":50.0,\"quantityChange\":10,\"filled\":true}",
                "{\"orderID\":\"67890\",\"userId\":\"userB\",\"price\":50.0,\"quantityChange\":10,\"filled\":true}"
        );

        when(mockOrderProcessor.processOrder(any(Order.class))).thenReturn(
                new ArrayList<>(fakeMatchesToBeProcessed)
        );

        ArrayList<Document> matchesFound =
                DatabaseUtils.processOrderAndParseMatchesFound(order, mockOrderProcessor);

        ArrayList<Document> expectedMatches = new ArrayList<>();
        expectedMatches.add(
                Document.parse(
                        "{\"orderID\":\"12345\",\"userId\":\"userA\",\"price\":50.0,\"quantityChange\":10,\"filled\":true}"
                )
        );
        expectedMatches.add(
                Document.parse(
                        "{\"orderID\":\"67890\",\"userId\":\"userB\",\"price\":50.0,\"quantityChange\":10,\"filled\":true}"
                )
        );

        assertEquals(expectedMatches, matchesFound);
    }

    @Test
    void testUpdateDbForFulfilledOrderNotPreviouslyPartiallyFilled() {
        Document buyOrderDoc = Document.parse(
                "{\"orderID\":\"12345\",\"userId\":\"userA\",\"price\":50.0,\"quantityChange\":10,\"filled\":true}"
        );
        Document sellOrderDoc = Document.parse(
                "{\"orderID\":\"67890\",\"userId\":\"userB\",\"price\":50.0,\"quantityChange\":10,\"filled\":true}"
        );

        ArrayList<Document> matchesFoundAsMongoDBDocs = new ArrayList<>(
                Arrays.asList(buyOrderDoc, sellOrderDoc)
        );

        FindIterable<Document> mockOrderHistoryFind = mock(FindIterable.class);
        when(mockOrderHistoryCollection.find((Bson) any())).thenReturn(
                mockOrderHistoryFind
        );
        when(mockOrderHistoryFind.first()).thenReturn(null);

        FindIterable<Document> mockActiveFind = mock(FindIterable.class);
        when(mockActiveOrdersCollection.find((Bson) any())).thenReturn(
                mockActiveFind
        );
        when(mockActiveFind.first()).thenReturn(buyOrderDoc, sellOrderDoc);

        DatabaseUtils.updateDb(
                matchesFoundAsMongoDBDocs,
                mockActiveOrdersCollection,
                mockUsersCollection,
                mockOrderHistoryCollection
        );

        verify(mockOrderHistoryCollection).insertOne(
                argThat(
                        doc ->
                                doc.getString("orderID").equals("12345") && doc.getBoolean("filled")
                )
        );

        verify(mockActiveOrdersCollection).deleteOne(
                eq(Filters.eq("orderId", "12345"))
        );

        verify(mockUsersCollection).updateOne(
                eq(Filters.eq("userId", "userA")),
                eq(new Document("$inc", new Document("balance", -500)))
        );

        verify(mockOrderHistoryCollection).insertOne(
                argThat(
                        doc ->
                                doc.getString("orderID").equals("67890") && doc.getBoolean("filled")
                )
        );

        verify(mockActiveOrdersCollection).deleteOne(
                eq(Filters.eq("orderId", "12345"))
        );

        verify(mockUsersCollection).updateOne(
                eq(Filters.eq("userId", "userB")),
                eq(new Document("$inc", new Document("balance", 500)))
        );
    }

    @Test
    void testUpdateDbForPartiallyFilledBuyOrderNotPreviouslyPartiallyFilled() {
        Document buyOrderDoc = Document.parse("{\"orderID\":\"12345\",\"userId\":\"userA\",\"price\":50.0,\"quantityChange\":20,\"filled\":false}");
        Document sellOrderDoc = Document.parse("{\"orderID\":\"67890\",\"userId\":\"userB\",\"price\":50.0,\"quantityChange\":20,\"filled\":true}");

        ArrayList<Document> matchesFoundAsMongoDBDocs = new ArrayList<>(
                Arrays.asList(buyOrderDoc, sellOrderDoc)
        );

        FindIterable<Document> mockOrderHistoryFind = mock(FindIterable.class);
        when(mockOrderHistoryCollection.find((Bson) any())).thenReturn(mockOrderHistoryFind);
        when(mockOrderHistoryFind.first()).thenReturn(null);

        FindIterable<Document> mockActiveFind = mock(FindIterable.class);
        when(mockActiveOrdersCollection.find((Bson) any())).thenReturn(mockActiveFind);
        when(mockActiveFind.first()).thenReturn(buyOrderDoc, sellOrderDoc);

        DatabaseUtils.updateDb(
                matchesFoundAsMongoDBDocs,
                mockActiveOrdersCollection,
                mockUsersCollection,
                mockOrderHistoryCollection
        );

        verify(mockOrderHistoryCollection).insertOne(argThat(doc ->
                doc.getString("orderID").equals("12345") &&
                        !doc.getBoolean("filled")));

        verify(mockActiveOrdersCollection).updateOne(
                eq(Filters.eq("orderId", "12345")),
                eq(new Document("$inc", new Document("quantity", -20)))
        );

        verify(mockUsersCollection).updateOne(
                eq(Filters.eq("userId", "userA")),
                eq(new Document("$inc", new Document("balance", -1000)))
        );

        verify(mockOrderHistoryCollection).insertOne(argThat(doc ->
                doc.getString("orderID").equals("67890") &&
                        doc.getBoolean("filled")));

        verify(mockActiveOrdersCollection).deleteOne(
                eq(Filters.eq("orderId", "67890"))
        );

        verify(mockUsersCollection).updateOne(
                eq(Filters.eq("userId", "userB")),
                eq(new Document("$inc", new Document("balance", 1000)))
        );

    }

    @Test
    void testUpdateDbForPartiallyFilledSellOrderNotPreviouslyPartiallyFilled() {
        Document buyOrderDoc = Document.parse("{\"orderID\":\"12345\",\"userId\":\"userA\",\"price\":50.0,\"quantityChange\":20,\"filled\":true}");
        Document sellOrderDoc = Document.parse("{\"orderID\":\"67890\",\"userId\":\"userB\",\"price\":50.0,\"quantityChange\":20,\"filled\":false}");

        ArrayList<Document> matchesFoundAsMongoDBDocs = new ArrayList<>(
                Arrays.asList(buyOrderDoc, sellOrderDoc)
        );

        FindIterable<Document> mockOrderHistoryFind = mock(FindIterable.class);
        when(mockOrderHistoryCollection.find((Bson) any())).thenReturn(mockOrderHistoryFind);
        when(mockOrderHistoryFind.first()).thenReturn(null);

        FindIterable<Document> mockActiveFind = mock(FindIterable.class);
        when(mockActiveOrdersCollection.find((Bson) any())).thenReturn(mockActiveFind);
        when(mockActiveFind.first()).thenReturn(buyOrderDoc, sellOrderDoc);

        DatabaseUtils.updateDb(
                matchesFoundAsMongoDBDocs,
                mockActiveOrdersCollection,
                mockUsersCollection,
                mockOrderHistoryCollection
        );

        verify(mockOrderHistoryCollection).insertOne(argThat(doc ->
                doc.getString("orderID").equals("12345") &&
                        doc.getBoolean("filled")));

        verify(mockActiveOrdersCollection).deleteOne(
                eq(Filters.eq("orderId", "12345"))
        );

        verify(mockUsersCollection).updateOne(
                eq(Filters.eq("userId", "userA")),
                eq(new Document("$inc", new Document("balance", -1000)))
        );

        verify(mockOrderHistoryCollection).insertOne(argThat(doc ->
                doc.getString("orderID").equals("67890") &&
                        !doc.getBoolean("filled")));

        verify(mockActiveOrdersCollection).updateOne(
                eq(Filters.eq("orderId", "67890")),
                eq(new Document("$inc", new Document("quantity", -20)))
        );

        verify(mockUsersCollection).updateOne(
                eq(Filters.eq("userId", "userB")),
                eq(new Document("$inc", new Document("balance", 1000)))
        );
    }

    @Test
    void testUpdateDbForFilledOrdersPreviouslyPartiallyFilledBuyOrder() {
        Document buyOrderDocHistory = Document.parse("{\"orderID\":\"12345\",\"userId\":\"userA\",\"price\":50.0,\"quantity\":100,\"filled\":false}");
        Document buyOrderDoc = Document.parse("{\"orderID\":\"12345\",\"userId\":\"userA\",\"price\":50.0,\"quantityChange\":80,\"filled\":true}");
        Document sellOrderDoc = Document.parse("{\"orderID\":\"67890\",\"userId\":\"userB\",\"price\":50.0,\"quantityChange\":80,\"filled\":true}");

        ArrayList<Document> matchesFoundAsMongoDBDocs = new ArrayList<>(
                Arrays.asList(buyOrderDoc, sellOrderDoc)
        );

        FindIterable<Document> mockOrderHistoryFind = mock(FindIterable.class);
        when(mockOrderHistoryCollection.find((Bson) any())).thenReturn(mockOrderHistoryFind);
        when(mockOrderHistoryFind.first()).thenReturn(buyOrderDocHistory, null);

        FindIterable<Document> mockActiveFind = mock(FindIterable.class);
        when(mockActiveOrdersCollection.find((Bson) any())).thenReturn(mockActiveFind);
        when(mockActiveFind.first()).thenReturn(buyOrderDoc, sellOrderDoc);

        DatabaseUtils.updateDb(
                matchesFoundAsMongoDBDocs,
                mockActiveOrdersCollection,
                mockUsersCollection,
                mockOrderHistoryCollection
        );

        verify(mockOrderHistoryCollection).updateOne(
                eq(Filters.eq("orderId", "12345")),
                eq(new Document("$set", new Document("filled", true)))
        );

        verify(mockActiveOrdersCollection).deleteOne(
                eq(Filters.eq("orderId", "12345"))
        );

        verify(mockUsersCollection).updateOne(
                eq(Filters.eq("userId", "userA")),
                eq(new Document("$inc", new Document("balance", -4000)))
        );

        verify(mockOrderHistoryCollection).insertOne(argThat(doc ->
                doc.getString("orderID").equals("67890") &&
                        doc.getBoolean("filled")));

        verify(mockActiveOrdersCollection).deleteOne(
                eq(Filters.eq("orderId", "67890"))
        );

        verify(mockUsersCollection).updateOne(
                eq(Filters.eq("userId","userB")),
                eq(new Document("$inc", new Document("balance", 4000)))
        );
    }

    @Test
    void testUpdateDbForPartiallyFilledBuyOrderPreviouslyPartiallyFilledBuyOrder(){

    }
}

