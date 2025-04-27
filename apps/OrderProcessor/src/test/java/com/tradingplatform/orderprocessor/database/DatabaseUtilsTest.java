package com.tradingplatform.orderprocessor.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.tradingplatform.orderprocessor.OrderProcessorService;
import com.tradingplatform.orderprocessor.orders.Order;
import com.tradingplatform.orderprocessor.orders.OrderType;
import com.tradingplatform.orderprocessor.orders.Ticker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

public class DatabaseUtilsTest {

  @Mock
  MongoCollection<Document> mockActiveOrdersCollection;

  @Mock
  MongoCollection<Document> mockUsersCollection;

  @Mock
  MongoCollection<Document> mockOrderHistoryCollection;

  @Mock
  OrderProcessorService mockOrderProcessor;

  @BeforeEach
  void setup() {
    mockActiveOrdersCollection = Mockito.mock(MongoCollection.class);
    mockUsersCollection = Mockito.mock(MongoCollection.class);
    mockOrderHistoryCollection = Mockito.mock(MongoCollection.class);
    mockOrderProcessor = Mockito.mock(OrderProcessorService.class);
  }

  @Test
  void testConvertMatchesToDocs() {
    ArrayList<String> fakeMatchesToBeProcessed = new ArrayList<>();
    fakeMatchesToBeProcessed.add(
      "{\"orderID\":\"12345\",\"userId\":\"userA\",\"price\":50.0,\"quantityChange\":10,\"filled\":true}"
    );

    fakeMatchesToBeProcessed.add(
      "{\"orderID\":\"67890\",\"userId\":\"userB\",\"price\":50.0,\"quantityChange\":10,\"filled\":true}"
    );

    ArrayList<Document> matchesFound = DatabaseUtils.convertMatchesToDocs(
      fakeMatchesToBeProcessed
    );

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
      "{\"orderID\":\"12345\",\"userId\":\"userA\",\"price\":50.0,\"quantityChange\":10,\"filled\":true,\"ticker\":\"AAPL\"}"
    );
    Document sellOrderDoc = Document.parse(
      "{\"orderID\":\"67890\",\"userId\":\"userB\",\"price\":50.0,\"quantityChange\":10,\"filled\":true,\"ticker\":\"AAPL\"}"
    );

    UpdateResult OK_RESULT = UpdateResult.acknowledged(1L, 1L, null);

    Mockito.when(
      mockUsersCollection.updateOne(
        ArgumentMatchers.any(Bson.class),
        ArgumentMatchers.any(Bson.class)
      )
    ).thenReturn(OK_RESULT, OK_RESULT);

    ArrayList<Document> matchesFoundAsMongoDBDocs = new ArrayList<>(
      Arrays.asList(buyOrderDoc, sellOrderDoc)
    );

    FindIterable<Document> mockOrderHistoryFind = Mockito.mock(
      FindIterable.class
    );
    Mockito.when(
      mockOrderHistoryCollection.find((Bson) ArgumentMatchers.any())
    ).thenReturn(mockOrderHistoryFind);
    Mockito.when(mockOrderHistoryFind.first()).thenReturn(null);

    FindIterable<Document> mockActiveFind = Mockito.mock(FindIterable.class);
    Mockito.when(
      mockActiveOrdersCollection.find((Bson) ArgumentMatchers.any())
    ).thenReturn(mockActiveFind);
    Mockito.when(mockActiveFind.first()).thenReturn(buyOrderDoc, sellOrderDoc);

    DatabaseUtils.updateDb(
      matchesFoundAsMongoDBDocs,
      mockActiveOrdersCollection,
      mockUsersCollection,
      mockOrderHistoryCollection
    );

    Mockito.verify(mockOrderHistoryCollection).insertOne(
      ArgumentMatchers.argThat(
        doc ->
          doc.getString("orderID").equals("12345") && doc.getBoolean("filled")
      )
    );

    Mockito.verify(mockActiveOrdersCollection).deleteOne(
      ArgumentMatchers.eq(Filters.eq("orderId", "12345"))
    );

    Mockito.verify(mockUsersCollection).updateOne(
      ArgumentMatchers.argThat((Bson filter) ->
        filter
          .toBsonDocument()
          .equals(
            Filters.and(
              Filters.eq("userId", "userA"),
              Filters.elemMatch("portfolio", Filters.eq("ticker", "AAPL"))
            ).toBsonDocument()
          )
      ),
      ArgumentMatchers.argThat((Bson update) ->
        update
          .toBsonDocument()
          .equals(
            Updates.combine(
              Updates.inc("balance", -500),
              Updates.inc("portfolio.$.quantity", 10)
            ).toBsonDocument()
          )
      )
    );

    Mockito.verify(mockOrderHistoryCollection).insertOne(
      ArgumentMatchers.argThat(
        doc ->
          doc.getString("orderID").equals("67890") && doc.getBoolean("filled")
      )
    );

    Mockito.verify(mockActiveOrdersCollection).deleteOne(
      ArgumentMatchers.eq(Filters.eq("orderId", "67890"))
    );

    Mockito.verify(mockUsersCollection).updateOne(
      ArgumentMatchers.argThat((Bson filter) ->
        filter
          .toBsonDocument()
          .equals(
            Filters.and(
              Filters.eq("userId", "userB"),
              Filters.elemMatch("portfolio", Filters.eq("ticker", "AAPL"))
            ).toBsonDocument()
          )
      ),
      ArgumentMatchers.argThat((Bson update) ->
        update
          .toBsonDocument()
          .equals(
            Updates.combine(
              Updates.inc("balance", 500),
              Updates.inc("portfolio.$.quantity", -10)
            ).toBsonDocument()
          )
      )
    );
  }

  @Test
  void testUpdateDbForFilledOrdersPreviouslyPartiallyFilledBuyOrder() {
    Document buyOrderDocHistory = Document.parse(
      "{\"orderID\":\"12345\",\"userId\":\"userA\",\"price\":50.0,\"quantity\":100,\"filled\":false,\"ticker\":\"AAPL\"}"
    );
    Document buyOrderDoc = Document.parse(
      "{\"orderID\":\"12345\",\"userId\":\"userA\",\"price\":50.0,\"quantityChange\":80,\"filled\":true,\"ticker\":\"AAPL\"}"
    );
    Document sellOrderDoc = Document.parse(
      "{\"orderID\":\"67890\",\"userId\":\"userB\",\"price\":50.0,\"quantityChange\":80,\"filled\":true,\"ticker\":\"AAPL\"}"
    );

    UpdateResult OK_RESULT = UpdateResult.acknowledged(1L, 1L, null);

    Mockito.when(
      mockUsersCollection.updateOne(
        ArgumentMatchers.any(Bson.class),
        ArgumentMatchers.any(Bson.class)
      )
    ).thenReturn(OK_RESULT, OK_RESULT);

    ArrayList<Document> matchesFoundAsMongoDBDocs = new ArrayList<>(
      Arrays.asList(buyOrderDoc, sellOrderDoc)
    );

    FindIterable<Document> mockOrderHistoryFind = Mockito.mock(
      FindIterable.class
    );
    Mockito.when(
      mockOrderHistoryCollection.find((Bson) ArgumentMatchers.any())
    ).thenReturn(mockOrderHistoryFind);
    Mockito.when(mockOrderHistoryFind.first()).thenReturn(
      buyOrderDocHistory,
      null
    );

    FindIterable<Document> mockActiveFind = Mockito.mock(FindIterable.class);
    Mockito.when(
      mockActiveOrdersCollection.find((Bson) ArgumentMatchers.any())
    ).thenReturn(mockActiveFind);
    Mockito.when(mockActiveFind.first()).thenReturn(buyOrderDoc, sellOrderDoc);

    DatabaseUtils.updateDb(
      matchesFoundAsMongoDBDocs,
      mockActiveOrdersCollection,
      mockUsersCollection,
      mockOrderHistoryCollection
    );

    Mockito.verify(mockOrderHistoryCollection).updateOne(
      ArgumentMatchers.eq(Filters.eq("orderId", "12345")),
      ArgumentMatchers.eq(new Document("$set", new Document("filled", true)))
    );

    Mockito.verify(mockActiveOrdersCollection).deleteOne(
      ArgumentMatchers.eq(Filters.eq("orderId", "12345"))
    );

    Mockito.verify(mockUsersCollection).updateOne(
      ArgumentMatchers.argThat((Bson filter) ->
        filter
          .toBsonDocument()
          .equals(
            Filters.and(
              Filters.eq("userId", "userA"),
              Filters.elemMatch("portfolio", Filters.eq("ticker", "AAPL"))
            ).toBsonDocument()
          )
      ),
      ArgumentMatchers.argThat((Bson update) ->
        update
          .toBsonDocument()
          .equals(
            Updates.combine(
              Updates.inc("balance", -4000),
              Updates.inc("portfolio.$.quantity", 80)
            ).toBsonDocument()
          )
      )
    );

    Mockito.verify(mockOrderHistoryCollection).insertOne(
      ArgumentMatchers.argThat(
        doc ->
          doc.getString("orderID").equals("67890") && doc.getBoolean("filled")
      )
    );

    Mockito.verify(mockActiveOrdersCollection).deleteOne(
      ArgumentMatchers.eq(Filters.eq("orderId", "67890"))
    );

    Mockito.verify(mockUsersCollection).updateOne(
      ArgumentMatchers.argThat((Bson filter) ->
        filter
          .toBsonDocument()
          .equals(
            Filters.and(
              Filters.eq("userId", "userB"),
              Filters.elemMatch("portfolio", Filters.eq("ticker", "AAPL"))
            ).toBsonDocument()
          )
      ),
      ArgumentMatchers.argThat((Bson update) ->
        update
          .toBsonDocument()
          .equals(
            Updates.combine(
              Updates.inc("balance", 4000),
              Updates.inc("portfolio.$.quantity", -80)
            ).toBsonDocument()
          )
      )
    );
  }

  @Test
  void testUpdateDbForPartiallyFilledBuyOrderPreviouslyPartiallyFilledBuyOrder() {}
  //TODO
  //test partial fill
  //test one complete BUY order following two partial SELL orders

}
