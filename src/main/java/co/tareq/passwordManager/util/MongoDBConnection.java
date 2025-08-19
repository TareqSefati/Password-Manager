package co.tareq.passwordManager.util;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static co.tareq.passwordManager.util.AppConstants.MONGO_DATABASE;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDBConnection {

    private static MongoDBConnection instance;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private String connectionString; // Store the connection string

    private MongoDBConnection() {
        // Private constructor to enforce Singleton pattern
    }

    public static synchronized MongoDBConnection getInstance() {
        if (instance == null) {
            instance = new MongoDBConnection();
        }
        return instance;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
        // Close existing client if any
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
        }
    }

    public MongoDatabase getDatabase() {
        if (database == null) {
            if (connectionString == null || connectionString.isEmpty()) {
                throw new IllegalStateException("MongoDB connection string not set. Please go to Settings.");
            }
            try {
                CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
                CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

                MongoClientSettings settings = MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(connectionString))
                        .codecRegistry(codecRegistry)
                        .build();

                mongoClient = MongoClients.create(settings);
                database = mongoClient.getDatabase(MONGO_DATABASE); // Your database name
            } catch (Exception e) {
                System.err.println("Error connecting to MongoDB: " + e.getMessage());
                throw new RuntimeException("Failed to connect to MongoDB. Check connection string and network.", e);
            }
        }
        return database;
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
        }
    }
}
