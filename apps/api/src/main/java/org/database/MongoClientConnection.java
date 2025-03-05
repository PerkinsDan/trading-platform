package org.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoClientConnection {
    public static void main(String[] args) {
        String connectionString = "mongodb+srv://databaserUser:databaseUser@trading-platform.beiiy.mongodb.net/?retryWrites=true&w=majority&appName=trading-platform";
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1) //sets version to stable API version
                .build();
        MongoClientSettings settings = MongoClientSettings.builder() //builder pattern that creates configuration object for MongoDB client
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Send a ping to confirm a successful connection
                MongoDatabase database = mongoClient.getDatabase("admin");
                database.runCommand(new Document("ping", 1));
                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }
}