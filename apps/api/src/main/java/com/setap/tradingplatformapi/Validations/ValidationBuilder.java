package com.setap.tradingplatformapi.Validations;

//import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
// import whatever you need for valdiations 
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
//import com.mongodb.client.model.Projections;
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
        validations.add(body -> 

            body.containsKey("userId") &&
            ! body.getString("userId").isBlank() &&
            usersCollection.find(Filters.eq("userId", body.getString("userId"))).first() != null
            
        );
        return this;
    }

    public ValidationBuilder validateOrderId(){
        MongoCollection<Document> activeOrdersCollection =
                            MongoClientConnection.getCollection("activeOrders");
        validations.add(body -> 
            body.containsKey("orderId") && 
            !body.getString("orderId").isBlank() &&
            activeOrdersCollection.find(Filters.eq("orderId", body.getString("orderId"))).first() != null

        );
        return this;
    }

    public ValidationBuilder validateOrderType(){

        validations.add(body ->
            body.containsKey("type")&&
            !body.getString("type").isBlank()&&
            ValidOrderTypes.contains(body.getString("types"))
        );
        return this;
    }

    public ValidationBuilder validateTicker(){

        validations.add(body ->
            body.containsKey("ticker")&&
            !body.getString("ticker").isBlank()&&
            ValidOrderTypes.contains(body.getString("ticker"))
        );
        return this;
    }

    //add more methods the above as needed, ie validateSufficientStockForSell(), validateSufficientFundsForBuy() ...

    public Validation build() {
        return body -> validations.stream().allMatch(validation -> {
            try {
                return validation.validate(body);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }
    

}
    