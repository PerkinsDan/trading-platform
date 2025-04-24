package com.setap.tradingplatformapi.Validations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.setap.tradingplatformapi.database.MongoClientConnection;
import orderProcessor.OrderType;
import orderProcessor.Ticker;

public class ValidationBuilder{

    private final List<Validation> validations = new ArrayList<>();
    
    private static final Set<String> ValidOrderTypes = 
    Arrays.stream(OrderType.values())
          .map(Enum::name)
          .collect(Collectors.toSet());
    
    private static final Set<String> ValidTickers= 
    Arrays.stream(Ticker.values())
        .map(Enum::name)
        .collect(Collectors.toSet());

    public ValidationBuilder validateUserId(){

        MongoCollection<Document> usersCollection =
        MongoClientConnection.getCollection("users"); 

        validations.add(body -> {
            if (!body.containsKey("userId") || body.getString("userId").isBlank()){
                return ValidationResult.fail("userId is missing or blank");
            }
            if (usersCollection.find(Filters.eq("userId", body.getString("userId"))).first() == null){
                return ValidationResult.fail("Invalid userId : no such user exists in the database");
            }
            return ValidationResult.ok();
        });
        return this;
    }

    public ValidationBuilder validateOrderId(){
        
        MongoCollection<Document> activeOrdersCollection =
        MongoClientConnection.getCollection("activeOrders");

        validations.add(body -> {
            if (!body.containsKey("orderId") || body.getString("orderId").isBlank()){
                return ValidationResult.fail("orderId is missing or blank");
            }
            if (activeOrdersCollection.find(Filters.eq("orderId", body.getString("orderId"))).first() == null){
                return ValidationResult.fail("Invalid orderId : no such order exists in the database");
            }
            return ValidationResult.ok();
        });

        return this;
    }

    public ValidationBuilder validateOrderType(){

        validations.add(body -> {
            if (!body.containsKey("type") || body.getString("type").isBlank()){
                return ValidationResult.fail("OrderType is missing or blank");
            }
            if (!ValidOrderTypes.contains(body.getString("type"))){
                return ValidationResult.fail("Invalid Order Type : Must be a BUY or SELL");
            }
            return ValidationResult.ok();
        });

        return this;
    }

    public ValidationBuilder validateTicker(){

        validations.add(body -> {
            if (!body.containsKey("ticker") || body.getString("ticker").isBlank()){
                return ValidationResult.fail("Ticker is missing or blank");
            }
            if (!ValidTickers.contains(body.getString("ticker"))){
                return ValidationResult.fail("Invalid Ticker : This is not traded");
            }
            return ValidationResult.ok();
        });
        return this;
    }

    //add more methods the above as needed, ie validateSufficientStockForSell(), validateSufficientFundsForBuy() ...

    public Validation build() {
        return body -> {
            for (Validation validation : validations) {
                ValidationResult result = validation.validate(body);
                if (!result.isValid) {
                    return result;
                }
            }
            return ValidationResult.ok();
        };
    }
    

}
    