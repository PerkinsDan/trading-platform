/*Note : mockStatic can behave erratically, for any tests that use a static mock for MongoClientConnection need a
'mockMongoClientCollection.close()' at the end or all the following tests that use the static mock will also fail
becuase the `mockMongoCLientConnection.close() was not reached`
 */
package com.tradingplatform.orderprocessor.validations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
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

    private MockedStatic<MongoClientConnection> mockMongoClientConnection;

    @BeforeEach
    public void setUp(){
        // define a valid request body we can modify for each test
        String json = "{"
        +"\"orderId\": \"TestOrderId\", "
        +"\"userId\": \"TestUserId\", "
        + "\"type\": \"BUY\", "
        + "\"ticker\": \"GOOGL\", "
        + "\"price\": 123, "
        + "\"quantity\": 30"
        + "}";

        requestBody = new JsonObject(json);

        MockitoAnnotations.openMocks(this);

        mockMongoClientConnection = mockStatic(MongoClientConnection.class);
    }

    @AfterEach
    public void reset(){
        mockMongoClientConnection.close();
    }
    
    // Test values for Tickers
    @Test
    public void failOnInvalidTicker_TickerIsMissing(){

        requestBody.remove("ticker");
        Validation validation = new ValidationBuilder().validateTicker().build();
        ValidationResult result = validation.validate(requestBody);

        assertFalse(result.isValid);
        assertEquals(result.errorMessage, "Validation Error : Ticker is missing or blank");
    }
    @Test
    public void failOnInvalidTicker_TickerIsEmpty(){

        requestBody.put("ticker", " ");
        Validation validation = new ValidationBuilder().validateTicker().build();
        ValidationResult result = validation.validate(requestBody);

        assertFalse(result.isValid);
        assertEquals(result.errorMessage,"Validation Error : Ticker is missing or blank");
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
        assertEquals(result.errorMessage, "Validation Error : order type is missing or blank");
    }
    public void failOnInvalidType_TypeIsEmpty(){

        requestBody.put("type", " ");
        Validation validation = new ValidationBuilder().validateOrderType().build();
        ValidationResult result = validation.validate(requestBody);

        assertFalse(result.isValid);
        assertEquals(result.errorMessage,"Validation Error : order type is missing or blank");
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
            assertEquals(result.errorMessage, "Validaton Error : orderId is missing or blank");
        }
        @Test
        public void failOnInvalidOrderId_OrderIdIsEmpty(){
    
            requestBody.put("orderId", " ");
            Validation validation = new ValidationBuilder().validateOrderId().build();
            ValidationResult result = validation.validate(requestBody);
    
            assertFalse(result.isValid);
            assertEquals(result.errorMessage,"Validaton Error : orderId is missing or blank");
        }


        @Test
        public void failOnInvalidOrderId_OrderIdDoesNotExist() {

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
    
            requestBody.remove("userId");
            Validation validation = new ValidationBuilder().validateUserId().build();
            ValidationResult result = validation.validate(requestBody);
    
            assertFalse(result.isValid);
            assertEquals(result.errorMessage, "Validation Error : userId is missing or blank");
        }

        @Test
        public void failOnInvalidUserId_UserIdIsEmpty(){
    
            requestBody.put("userId", " ");
            Validation validation = new ValidationBuilder().validateUserId().build();
            ValidationResult result = validation.validate(requestBody);
    
            assertFalse(result.isValid);
            assertEquals(result.errorMessage,"Validation Error : userId is missing or blank");
        }

        @Test
        public void failOnInvalidUserId_UserIdDoesNotExist() {

            mockMongoClientConnection.when(() -> MongoClientConnection.getCollection("users")).thenReturn(usersCollection);

            when(usersCollection.find(Filters.eq("userId", "TestUserId"))).thenReturn(mockFindIterable);
            when(mockFindIterable.first()).thenReturn(null);

            ValidationBuilder validationBuilder = new ValidationBuilder().validateUserId();
            ValidationResult result = validationBuilder.build().validate(requestBody);

            assertFalse(result.isValid);
            assertEquals("Invalid userId : no such user exists in the database", result.errorMessage);  
        }

        @Test
        public void passOnValidUserId() {

            mockMongoClientConnection.when(() -> MongoClientConnection.getCollection("users")).thenReturn(usersCollection);

            when(usersCollection.find(Filters.eq("userId", "TestUserId"))).thenReturn(mockFindIterable);
            when(mockFindIterable.first()).thenReturn(new Document());

            ValidationBuilder validationBuilder = new ValidationBuilder().validateUserId();
            ValidationResult result = validationBuilder.build().validate(requestBody);

            assertTrue(result.isValid);
            assertNull(result.errorMessage);   
        }

        @Test
        public void failOnInvalidQuantity_QuantityisEmpty(){
    
            requestBody.put("quantity", " ");
            Validation validation = new ValidationBuilder().validateQuantity().build();
            ValidationResult result = validation.validate(requestBody);
    
            assertFalse(result.isValid);
            assertEquals(result.errorMessage,"Validation Error : Quantity is missing or blank");
        }






}