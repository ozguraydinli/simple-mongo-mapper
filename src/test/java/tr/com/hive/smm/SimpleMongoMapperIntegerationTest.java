package tr.com.hive.smm;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.Date;

import tr.com.hive.smm.model.ClassA;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SimpleMongoMapperIntegerationTest {

  @Test
  void test_SingleField() {
    TestHelper.withMongoClient(mongoClient -> {
      MongoDatabase database = mongoClient.getDatabase("sample");

      MongoCollection<Document> collection = database.getCollection("sample2");

      Document document = new Document();
      ObjectId id = new ObjectId();

      document.put("_id", id);

      Date now = new Date();
      document.put("varDate", now);
      document.put("varInstant", now);

      collection.insertOne(document);

      Document fromDb = collection.find().first();

      assertNotNull(fromDb);

      assertEquals(new ObjectId(id.toString()), fromDb.getObjectId("_id"));
      assertEquals(now, fromDb.getDate("varDate"));
      assertEquals(now, fromDb.getDate("varInstant"));

      SimpleMongoMapper simpleMongoMapper = new SimpleMongoMapper();
      ClassA classA = simpleMongoMapper.fromDocument(document, ClassA.class);

      assertEquals(id, classA.id);
      assertEquals(now, classA.varDate);
      assertEquals(now.toInstant(), classA.varInstant);
    });
  }

}
