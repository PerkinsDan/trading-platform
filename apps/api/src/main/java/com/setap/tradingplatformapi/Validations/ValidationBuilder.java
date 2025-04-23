package com.setap.tradingplatformapi.Validations;

//import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
// import whatever you need for valdiations 

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
//import com.mongodb.client.model.Projections;
import com.setap.tradingplatformapi.database.MongoClientConnection;

public class ValidationBuilder{

    private final List<Validation> validations = new ArrayList<>();

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

    //add more methods the above two as needed, ie validateSufficientStockForSell(), validateSufficientFundsForBuy() ...

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
    