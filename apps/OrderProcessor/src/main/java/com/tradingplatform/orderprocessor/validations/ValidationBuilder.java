package com.tradingplatform.orderprocessor.validations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.tradingplatform.orderprocessor.database.MongoClientConnection;
import com.tradingplatform.orderprocessor.orders.OrderType;
import com.tradingplatform.orderprocessor.orders.Ticker;

import io.vertx.core.json.JsonObject;

import static com.tradingplatform.orderprocessor.database.DatabaseUtils.*;

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

    private Boolean missingOrEmpty(JsonObject body, String attribute){

            if (!body.containsKey(attribute) || body.getString(attribute).isBlank()){
                return true;
            }
            return false;
    }

    public ValidationBuilder validateAttribute(String attribute){
        // wrapper to access missingOrEmpty outside the Builder class
        validations.add(body -> {
            if (!missingOrEmpty(body, attribute)){
                return ValidationResult.ok();
            } else {
                return ValidationResult.fail("Validation Error : " + attribute + " is missing or blank");
            }
        });
        return this;
    }

    public ValidationBuilder validatePrice(){
        validations.add(body -> {
            if(!missingOrEmpty(body, "price"))
                try {
                    Double price = Double.parseDouble(body.getString("price"));
                    if (price <= 0){
                        return ValidationResult.fail("Price cannot be equal to or less than 0.");
                    }
                } catch (Exception e) {
                    if (e instanceof NumberFormatException){
                        return ValidationResult.fail("Validation Error : Price is invalid.");
                    }
                }
            else{
                return ValidationResult.fail("Validation Error : Price is missing or blank");

            }

            return ValidationResult.ok();

        });
        return this;
    }

    public ValidationBuilder validateQuantity(){

        validations.add(body -> {
            if(!missingOrEmpty(body,"quantity")){
                try{
                    int quantity = Integer.parseInt(body.getString("quantity"));
                    if (quantity <= 0){
                        return ValidationResult.fail("Price cannot be equal to or less than 0.");
                    }
                } catch (Exception e){
                    if (e instanceof NumberFormatException){
                        return ValidationResult.fail("Validation Error : Quantity is invalid ");
                    }
                }
            } else {
                return ValidationResult.fail("Validation Error : Quantity is missing or blank");
            }
            return ValidationResult.ok();
        });
        return this;

    }

    public ValidationBuilder validateUserId(){

        validations.add(body -> {
            if (!missingOrEmpty(body,"userId")){
                MongoCollection<Document> usersCollection =
                MongoClientConnection.getCollection("users"); 
                if (usersCollection.find(Filters.eq("userId", body.getString("userId"))).first() == null){
                    return ValidationResult.fail("Invalid userId : no such user exists in the database");
                }
                return ValidationResult.ok();

            } else {
                return ValidationResult.fail("Validation Error : userId is missing or blank");
            }
        });
        return this;
    }

    public ValidationBuilder validateOrderId(){
        
        validations.add(body -> {
            if (!missingOrEmpty(body,"orderId")){
                MongoCollection<Document> activeOrdersCollection =
                MongoClientConnection.getCollection("activeOrders");
                if (activeOrdersCollection.find(Filters.eq("orderId", body.getString("orderId"))).first() == null){
                    return ValidationResult.fail("Invalid orderId : no such order exists in the database");
                }
            } else {
                return ValidationResult.fail("Validaton Error : orderId is missing or blank");
            }
            return ValidationResult.ok();
        });
        return this;
    }

    public ValidationBuilder validateOrderType(){

        validations.add(body -> {
            if (!missingOrEmpty(body,"type")){
                if (!ValidOrderTypes.contains(body.getString("type"))){
                    return ValidationResult.fail("Invalid Order Type : Must be a BUY or SELL");
                }
            } else {
                return ValidationResult.fail("Validation Error : order type is missing or blank");
            }
            return ValidationResult.ok();
        });
        return this;
    }

    public ValidationBuilder validateTicker(){

        validations.add(body -> {
            if (!missingOrEmpty(body,"ticker")){
                if (!ValidTickers.contains(body.getString("ticker"))){
                    return ValidationResult.fail("Invalid Ticker : This is not traded");
                }
            } else {
                return ValidationResult.fail("Validation Error : Ticker is missing or blank");
            }
            return ValidationResult.ok();
        });
        return this;
    }
    public ValidationBuilder validateUserBalanceAndPorfolio(){
        validations.add(body ->{
            if (body.getString("type").equals("SELL")){
                    return userPortfolioIsSufficientForSell(body);
            } else{
                return userBalanceIsSufficientForBuy(body);
            }
        });
        return this;
    }

    public ValidationBuilder validateOrderToCancelBelongsToUser(){
        MongoCollection<Document> activeOrderCollection = MongoClientConnection.getCollection("activeOrders");

        validations.add(body -> {
            Document orderDoc = activeOrderCollection
            .find(Filters.eq("orderId", body.getString("orderId")))
            .first();

            if(orderDoc != null){
                if (orderDoc.getString("userId").equals(body.getString("userId"))){
                    return ValidationResult.ok();
                } else {
                    return ValidationResult.fail("Error : The order you are trying to cancel doesnt belong to you");
                }
            } else {
                return ValidationResult.fail("Invalid Order Id - No such order could be found.");
            }
        });
        return this;
    }

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
    