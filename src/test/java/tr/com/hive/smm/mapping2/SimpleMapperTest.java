package tr.com.hive.smm.mapping2;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import tr.com.hive.smm.model.ClassA1;
import tr.com.hive.smm.model.ClassA1.EmbeddedA1;
import tr.com.hive.smm.model.ClassB;
import tr.com.hive.smm.model.MyComplexEnum;
import tr.com.hive.smm.model.MyEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleMapperTest {

//  @Test
//  void testSimpleMapper2() {
//    withMongoClient(mongoClient -> {
//      SimpleMapper simpleMapper = SimpleMapper.builder()
//                                              .forPackage("tr.com.hive.smm.model")
//                                              .forClass(ClassA1.class)
//                                              .forClass(EmbeddedA1.class)
//                                              .forClass(ClassB.class)
//                                              .build();
//
//      CodecRegistry codecRegistry = simpleMapper.getCodecRegistry();
//
//      MongoDatabase database = mongoClient.getDatabase("sample")
//                                          .withCodecRegistry(codecRegistry);
//
//      MongoCollection<ClassA1> collection = database.getCollection(ClassA1.class.getSimpleName(), ClassA1.class);
//
//      ClassA1 document = new ClassA1();
//      document.varField = new MyInterfaceImpl("zxc");
//
//      collection.insertOne(document);
//    });
//  }

  @Test
  void testSimpleMapper() {
    withMongoClient(mongoClient -> {

      SimpleMapper simpleMapper = SimpleMapper.builder()
                                              .forPackage("tr.com.hive.smm.model")
                                              .forClass(ClassA1.class)
                                              .forClass(EmbeddedA1.class)
                                              .forClass(ClassB.class)
                                              .build();

      CodecRegistry codecRegistry = simpleMapper.getCodecRegistry();

      MongoDatabase database = mongoClient.getDatabase("sample")
                                          .withCodecRegistry(codecRegistry);

      MongoCollection<ClassA1> collection = database.getCollection(ClassA1.class.getSimpleName(), ClassA1.class);

      ClassA1 classA1 = new ClassA1();

      ObjectId id = new ObjectId();
      classA1.id = id;
      classA1.varString = "myVal";
      classA1.varNullString = null;
      classA1.varEnum = MyEnum.En1;
      classA1.varInt = 1;
      classA1.varBoxedInt = 2;

      ObjectId varId = new ObjectId();
      classA1.varObjectId = varId;

      Date now = new Date();
      classA1.varDate = now;
      classA1.varInstant = now.toInstant();

      classA1.varStringTransient = "asdasdasd";

      ObjectId classBId2 = new ObjectId();
      ClassB classB2 = new ClassB(classBId2);
      classB2.varString = "str";
      classB2.varInt = 3;
      Date now2 = new Date();
      classB2.varDate = now2;
      classB2.varEnum = MyEnum.En2;

      classA1.varClassB = classB2;

      classA1.varStringSuper = "myVal";
      classA1.setVarIntSuper(1);

      BigDecimal varBigDecimal = new BigDecimal(13);
      classA1.varBigDecimal = varBigDecimal;

      // collections

      classA1.varListOfString = List.of("a1", "a2", "a3");
      classA1.varListOfInteger1 = List.of(1, 2, 3);
      Date now3 = new Date();
      classA1.varListOfDate = List.of(now3);
      classA1.varListOfEnum = List.of(MyEnum.En1, MyEnum.En2);
      classA1.varSetOfEnum = Set.of(MyEnum.En1, MyEnum.En2);
      classA1.varSetOfString = Set.of("a1", "a2", "a3");
      classA1.varListOfComplexEnum = List.of(MyComplexEnum.ComplexEn1);

      ObjectId id1 = new ObjectId();
      ObjectId id2 = new ObjectId();
      classA1.varListOfObjectId = List.of(id1, id2);
      classA1.varListOfClassB = List.of(ClassB.create(1), ClassB.create(2));
      classA1.varListOfListOfString = List.of(
        List.of("s1", "s2"),
        List.of("s3")
      );
      classA1.varListOfListOfClassB = List.of(
        List.of(ClassB.create(1), ClassB.create(2)),
        List.of(ClassB.create(3))
      );

      // maps
      classA1.varMapOfString = Map.of("k1", "v1", "k2", "v2");
      classA1.varMapOfString2 = Map.of();
      classA1.varMapOfEnumString = Map.of(MyEnum.En1, "v1");
      ObjectId id3 = new ObjectId();
      classA1.varMapOfObjectIdString = Map.of(id3, "v1");
      classA1.varMapOfIntegerString = Map.of(1, "v1", 2, "v2", 3, "v3");
      classA1.varMapOfListOfString = Map.of("k1", List.of("v1", "v2"));
      classA1.varMapOfListOfClassB = Map.of("k1", List.of(ClassB.create(3)));
      ObjectId id4 = new ObjectId();
      classA1.varMapOfObjectId = Map.of("k1", id4);
      Date dateV1 = new Date();
      classA1.varMapOfDate = Map.of("k1", dateV1);

      classA1.varNestedMapWithCustomCodec = Map.of(
        "k1", List.of(
          Map.of("k11", "v11"),
          Map.of("k12", "v12")
        ),
        "k2", List.of(
          Map.of("k21", "v21"),
          Map.of("k22", "v22"),
          Map.of("k23", "v23")
        )
      );

      // mongorefs
      ObjectId classBId = new ObjectId();
      ClassB classB = new ClassB(classBId);
      classB.varString = "str";
      classA1.refClassB = classB;

      ObjectId id5 = new ObjectId();
      ObjectId id6 = new ObjectId();
      classA1.refListOfClassA = List.of(
        ClassA1.create(id5, "ss"),
        ClassA1.create(id6, "s1")
      );

      collection.insertOne(classA1);

      ClassA1 fromDb = collection.find(Filters.eq("_id", classA1.id)).first();

      Objects.requireNonNull(fromDb);

      assertEquals(new ObjectId(id.toString()), fromDb.id);
      assertEquals("myVal", fromDb.varString);
      assertNull(fromDb.varNullString);
      assertEquals(MyEnum.En1, fromDb.varEnum);
      assertEquals(1, fromDb.varInt);
      assertEquals(2, fromDb.varBoxedInt);
      assertEquals(new ObjectId(varId.toString()), fromDb.varObjectId);
      assertEquals(new Date(now.getTime()), fromDb.varDate);
      assertEquals(Instant.from(now.toInstant()), fromDb.varInstant);
      assertNull(fromDb.varStringTransient);

      assertNotNull(fromDb.varClassB);
      assertNotNull(fromDb.varClassB.id);
      assertNotNull(fromDb.varClassB.varString);
      assertEquals(new ObjectId(classBId2.toString()), fromDb.varClassB.id);
      assertEquals("str", fromDb.varClassB.varString);
      assertEquals(new Date(now2.getTime()), fromDb.varClassB.varDate);
      assertEquals(MyEnum.En2, fromDb.varClassB.varEnum);

      assertEquals("myVal", fromDb.varStringSuper);
      assertEquals(1, fromDb.getVarIntSuper());

      assertEquals(new BigDecimal(varBigDecimal.toBigInteger()), fromDb.varBigDecimal);

      assertEquals(3, fromDb.varListOfString.size());
      assertEquals(List.of("a1", "a2", "a3"), fromDb.varListOfString);
      assertEquals(3, fromDb.varListOfInteger1.size());
      assertEquals(List.of(1, 2, 3), fromDb.varListOfInteger1);
      assertEquals(1, fromDb.varListOfDate.size());
      assertEquals(new Date(now3.getTime()), fromDb.varListOfDate.getFirst());
      assertEquals(2, fromDb.varListOfEnum.size());
      assertEquals(MyEnum.En1, fromDb.varListOfEnum.getFirst());
      assertEquals(2, fromDb.varSetOfEnum.size());
      assertTrue(fromDb.varSetOfEnum.contains(MyEnum.En1));
      assertEquals(3, fromDb.varSetOfString.size());
      assertEquals(Set.of("a1", "a2", "a3"), fromDb.varSetOfString);
      assertEquals(1, fromDb.varListOfComplexEnum.size());
      assertEquals(List.of(MyComplexEnum.ComplexEn1), fromDb.varListOfComplexEnum);
      assertEquals(2, fromDb.varListOfObjectId.size());
      assertEquals(List.of(id1, id2), fromDb.varListOfObjectId);
      assertEquals(2, fromDb.varListOfClassB.size());
      assertEquals(1, fromDb.varListOfClassB.getFirst().varInt);
      assertEquals(2, fromDb.varListOfListOfString.size());
      assertEquals(2, fromDb.varListOfListOfString.getFirst().size());
      assertEquals("s1", fromDb.varListOfListOfString.getFirst().getFirst());
      assertEquals(1, fromDb.varListOfListOfString.get(1).size());
      assertEquals("s3", fromDb.varListOfListOfString.get(1).getFirst());
      assertEquals(2, fromDb.varListOfListOfClassB.size());
      assertEquals(2, fromDb.varListOfListOfClassB.getFirst().size());
      assertEquals(1, fromDb.varListOfListOfClassB.getFirst().getFirst().varInt);
      assertEquals(1, fromDb.varListOfListOfClassB.get(1).size());
      assertEquals(3, fromDb.varListOfListOfClassB.get(1).getFirst().varInt);
      assertNull(fromDb.varListOfClassB_Null);
      assertEquals(0, fromDb.varListOfClassB_Empty.size());

      assertEquals(2, fromDb.varMapOfString.size());
      assertEquals("v1", fromDb.varMapOfString.get("k1"));
//      assertNull(fromDb.varMapOfString2);
      assertEquals(1, fromDb.varMapOfEnumString.size());
      assertEquals("v1", fromDb.varMapOfEnumString.get(MyEnum.En1));
      assertNull(fromDb.varMapOfEnumString.get(MyEnum.En2));
      assertEquals(1, fromDb.varMapOfObjectIdString.size());
      assertEquals("v1", fromDb.varMapOfObjectIdString.get(id3));
      assertEquals(3, fromDb.varMapOfIntegerString.size());
      assertEquals("v1", fromDb.varMapOfIntegerString.get(1));
      assertEquals(1, fromDb.varMapOfListOfString.size());
      assertEquals(2, fromDb.varMapOfListOfString.get("k1").size());
      assertEquals("v1", fromDb.varMapOfListOfString.get("k1").getFirst());
      assertEquals(1, fromDb.varMapOfListOfClassB.size());
      assertEquals(1, fromDb.varMapOfListOfClassB.get("k1").size());
      assertEquals(3, fromDb.varMapOfListOfClassB.get("k1").getFirst().varInt);
      assertEquals(1, fromDb.varMapOfObjectId.size());
      assertEquals(id4, fromDb.varMapOfObjectId.get("k1"));
      assertEquals(1, fromDb.varMapOfDate.size());
      assertEquals(dateV1, fromDb.varMapOfDate.get("k1"));

      // mongorefs
      assertNotNull(fromDb.refClassB);
      assertNotNull(fromDb.refClassB.id);
      assertNull(fromDb.refClassB.varString);
      assertEquals(new ObjectId(classBId.toString()), fromDb.refClassB.id);

      assertEquals(2, fromDb.refListOfClassA.size());
      assertEquals(id5, fromDb.refListOfClassA.getFirst().id);
      assertNull(fromDb.refListOfClassA.getFirst().varString);

      assertNotNull(fromDb.refListOfClassBEmptyWithInit);

      // custom converter
      assertEquals(2, fromDb.varNestedMapWithCustomCodec.size());
      assertEquals(1, fromDb.varNestedMapWithCustomCodec.get("k1").getFirst().size());
      assertNotNull(fromDb.varNestedMapWithCustomCodec.get("k1").getFirst().get("k11" + 123));

    });
  }

  public void withMongoClient(Consumer<MongoClient> testBody) {
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