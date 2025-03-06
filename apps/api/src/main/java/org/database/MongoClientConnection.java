package org.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;

public class MongoClientConnection {

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static MongoCollection<Document> getOrdersCollection() {
        if (mongoClient == null) {
            initClient();
        }
        return database.getCollection("orders");
    }

    private static void initClient() {
        Dotenv dotenv = Dotenv.load();
        String connectionString = dotenv.get("DB_URI");
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        try {
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase("tradingPlatform");
            database.runCommand(new Document("ping", 1));
            System.out.println("Successfully connected to MongoDB!");
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        initClient();
    }
}