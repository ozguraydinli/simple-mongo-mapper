package tr.com.hive.smm;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.testcontainers.containers.MongoDBContainer;

import java.time.Duration;
import java.util.function.Consumer;

public class TestHelper {

  public static void withMongoClient(Consumer<MongoClient> testBody) {
    try (MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.5")) {

      mongoDBContainer.withStartupTimeout(Duration.ofSeconds(180L))
                      .start();

      System.out.println("Mongodb started: " + mongoDBContainer.getConnectionString());

      try (MongoClient mongoClient = MongoClients.create(
        MongoClientSettings.builder()
                           .applyConnectionString(new ConnectionString(mongoDBContainer.getConnectionString()))
                           .writeConcern(WriteConcern.ACKNOWLEDGED)
                           .build()
      )) {

        testBody.accept(mongoClient);
      }
    }
  }

}
