package tr.com.hive.smm;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.mongodb.MongoDBContainer;

import java.time.Duration;
import java.util.function.Consumer;

public class TestHelper {

  public static void withMongoClient(Consumer<MongoClient> testBody) {
    try (org.testcontainers.mongodb.MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:9.0.2")
      .withReplicaSet()
      .waitingFor(Wait.forListeningPort())
      .withStartupTimeout(Duration.ofSeconds(180L))
    ) {

      mongoDBContainer.withStartupTimeout(Duration.ofSeconds(180L))
                      .start();

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
