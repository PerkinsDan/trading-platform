package com.tradingplatform.orderprocessor.validations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.tradingplatform.orderprocessor.database.MongoClientConnection;

import io.vertx.core.json.JsonObject;

import org.bson.Document;

public class ValidationBuilderTest{

    private JsonObject requestBody;

    @Mock
    private MongoCollection<Document> usersCollection;

    @Mock
    private MongoCollection<Document> activeOrdersCollection;

    @Mock
    private FindIterable<Document> mockResultSet;

    @Mock
    private MongoDatabase database;

    @Mock
    private FindIterable<Document> mockFindIterable;

    @BeforeEach
    public void setUp(){
        // define a valid request body we can modify for each test
        String json = "{"
        +"\"orderId\": \"TestOrderId\", "
        +"\"userId\": \"TestUserId\", "
        + "\"type\": \"BUY\", "
        + "\"ticker\": \"GOOGL\", "
        + "\"price\": 123, "
        + "\"quantity\": 30, "
        + "\"userId\": \"userB\""
        + "}";

        requestBody = new JsonObject(json);

        MockitoAnnotations.openMocks(this);
    }
    
    // Test values for Tickers
    @Test
    public void failOnInvalidTicker_TickerIsMissing(){

        requestBody.remove("ticker");
        Validation validation = new ValidationBuilder().validateTicker().build();
        ValidationResult result = validation.validate(requestBody);

        assertFalse(result.isValid);
        assertEquals(result.errorMessage, "Ticker is missing or blank");
    }
    @Test
    public void failOnInvalidTicker_TickerIsEmpty(){

        requestBody.put("ticker", " ");
        Validation validation = new ValidationBuilder().validateTicker().build();
        ValidationResult result = validation.validate(requestBody);

        assertFalse(result.isValid);
        assertEquals(result.errorMessage,"Ticker is missing or blank");
    }

    @Test
    public void failOnInvalidTicker_TickerIsNotTraded(){
        requestBody.put("ticker","TEST");
        Validation validation = new ValidationBuilder().validateTicker().build();
        ValidationResult result = validation.validate(requestBody);

        assertFalse(result.isValid);
        assertEquals(result.errorMessage,"Invalid Ticker : This is not traded");
    }

    @Test
    public void passOnValidTicker(){

        Validation validation = new ValidationBuilder().validateTicker().build();
        ValidationResult result = validation.validate(requestBody);

        assertTrue(result.isValid);
        assertNull(result.errorMessage);
    }
    
    // test values for OrderTypes
    @Test
    public void failOnInvalidType_TypeIsMissing(){

        requestBody.remove("type");
        Validation validation = new ValidationBuilder().validateOrderType().build();
        ValidationResult result = validation.validate(requestBody);

        assertFalse(result.isValid);
        assertEquals(result.errorMessage, "OrderType is missing or blank");
    }
    @Test
    public void failOnInvalidType_TypeIsEmpty(){

        requestBody.put("type", " ");
        Validation validation = new ValidationBuilder().validateOrderType().build();
        ValidationResult result = validation.validate(requestBody);

        assertFalse(result.isValid);
        assertEquals(result.errorMessage,"OrderType is missing or blank");
    }

    @Test
    public void failOnInvalidType_TypeIsNotBuyOrSell(){
        requestBody.put("type","TEST");
        Validation validation = new ValidationBuilder().validateOrderType().build();
        ValidationResult result = validation.validate(requestBody);

        assertFalse(result.isValid);
        assertEquals(result.errorMessage,"Invalid Order Type : Must be a BUY or SELL");
    }

    @Test
    public void passOnValidType(){

        Validation validation = new ValidationBuilder().validateOrderType().build();
        ValidationResult result = validation.validate(requestBody);

        assertTrue(result.isValid);
        assertNull(result.errorMessage);
    }

        
        @Test
        public void failOnInvalidOrderId_OrderIdIsMissing(){
    
            requestBody.remove("orderId");
            Validation validation = new ValidationBuilder().validateOrderId().build();
            ValidationResult result = validation.validate(requestBody);
    
            assertFalse(result.isValid);
            assertEquals(result.errorMessage, "OrderType is missing or blank");
        }
        @Test
        public void failOnInvalidOrderId_OrderIdIsEmpty(){
    
            requestBody.put("orderId", " ");
            Validation validation = new ValidationBuilder().validateOrderId().build();
            ValidationResult result = validation.validate(requestBody);
    
            assertFalse(result.isValid);
            assertEquals(result.errorMessage,"OrderType is missing or blank");
        }


        @Test
        public void failOnInvalidOrderId_OrderIdDoesNotExist() {

            MockedStatic<MongoClientConnection> mockMongoClientConnection = mockStatic(MongoClientConnection.class);

            mockMongoClientConnection.when(() -> MongoClientConnection.getCollection("activeOrders")).thenReturn(activeOrdersCollection);

            when(activeOrdersCollection.find(Filters.eq("orderId", "TestOrderId"))).thenReturn(mockFindIterable);
            when(mockFindIterable.first()).thenReturn(null);

            ValidationBuilder validationBuilder = new ValidationBuilder().validateOrderId();
            ValidationResult result = validationBuilder.build().validate(requestBody);

            assertFalse(result.isValid);
            assertEquals("Invalid orderId : no such order exists in the database", result.errorMessage);  
        }

        @Test
        public void passOnValidOrderId() {

            MockedStatic<MongoClientConnection> mockMongoClientConnection = mockStatic(MongoClientConnection.class);

            mockMongoClientConnection.when(() -> MongoClientConnection.getCollection("activeOrders")).thenReturn(activeOrdersCollection);

            when(activeOrdersCollection.find(Filters.eq("orderId", "TestOrderId"))).thenReturn(mockFindIterable);
            when(mockFindIterable.first()).thenReturn(new Document());

            ValidationBuilder validationBuilder = new ValidationBuilder().validateOrderId();
            ValidationResult result = validationBuilder.build().validate(requestBody);

            assertTrue(result.isValid);
            assertNull(result.errorMessage);  
        }

        //test values for userId        
        @Test
        public void failOnInvalidUserId_UserIdIsMissing(){
    
            requestBody.remove("orderId");
            Validation validation = new ValidationBuilder().validateOrderType().build();
            ValidationResult result = validation.validate(requestBody);
    
            assertFalse(result.isValid);
            assertEquals(result.errorMessage, "OrderType is missing or blank");
        }

        @Test
        public void failOnInvalidUserId_UserIdIsEmpty(){
    
            requestBody.put("orderId", " ");
            Validation validation = new ValidationBuilder().validateOrderType().build();
            ValidationResult result = validation.validate(requestBody);
    
            assertFalse(result.isValid);
            assertEquals(result.errorMessage,"OrderType is missing or blank");
        }

        @Test
        public void failOnInvalidUserId_UserIdDoesNotExist() {

            MockedStatic<MongoClientConnection> mockMongoClientConnection = mockStatic(MongoClientConnection.class);

            mockMongoClientConnection.when(() -> MongoClientConnection.getCollection("users")).thenReturn(usersCollection);

            when(usersCollection.find(Filters.eq("userId", "TestUserId"))).thenReturn(mockFindIterable);
            when(mockFindIterable.first()).thenReturn(null);

            ValidationBuilder validationBuilder = new ValidationBuilder().validateUserId();
            ValidationResult result = validationBuilder.build().validate(requestBody);

            assertFalse(result.isValid);
            assertEquals("Invalid orderId : no such order exists in the database", result.errorMessage);  
        }

        @Test
        public void passOnValidUserId() {

            MockedStatic<MongoClientConnection> mockMongoClientConnection = mockStatic(MongoClientConnection.class);

            mockMongoClientConnection.when(() -> MongoClientConnection.getCollection("users")).thenReturn(usersCollection);

            when(usersCollection.find(Filters.eq("userId", "TestUserId"))).thenReturn(mockFindIterable);
            when(mockFindIterable.first()).thenReturn(new Document());

            ValidationBuilder validationBuilder = new ValidationBuilder().validateUserId();
            ValidationResult result = validationBuilder.build().validate(requestBody);

            assertTrue(result.isValid);
            assertNull(result.errorMessage);  
        }






}