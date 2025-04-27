package com.tradingplatform.orderprocessor.validations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tradingplatform.orderprocessor.OrderProcessorService;
import com.tradingplatform.orderprocessor.validations.*;

import io.vertx.core.json.JsonObject;

import org.bson.Document;

public class ValidationsTest{

    private JsonObject requestBody;

    @BeforeEach
    public void setUp(){
        // define a valid request body we can modify for each test
        String json = "{"
        + "\"type\": \"BUY\", "
        + "\"ticker\": \"GOOGL\", "
        + "\"price\": 123, "
        + "\"quantity\": 30, "
        + "\"userId\": \"userB\""
        + "}";

        requestBody = new JsonObject(json);
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



}