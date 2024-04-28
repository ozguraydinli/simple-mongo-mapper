package tr.com.hive.smm.mapping2;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import tr.com.hive.smm.TestHelper;
import tr.com.hive.smm.model.ClassA1;
import tr.com.hive.smm.model.ClassB;
import tr.com.hive.smm.model.ClassB2;
import tr.com.hive.smm.model.ClassBRef;
import tr.com.hive.smm.model.MyComplexEnum;
import tr.com.hive.smm.model.MyEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleMapperTest {

  @Test
  void testSimpleMapper2() {
//    System.out.println(Instant.now());
//    System.out.println(OffsetDateTime.now());
//    System.out.println(ZonedDateTime.now());
//
//    System.out.println(LocalDate.now());
//    System.out.println(LocalDateTime.now());
//
//    Clock clock = Clock.fixed(Instant.parse("2024-04-20T14:07:01.291283Z"), ZoneId.of("Europe/Berlin"));
//    Clock clock1 = Clock.fixed(Instant.parse("2024-04-20T18:07:01.291283Z"), ZoneId.of("Europe/Istanbul"));
//
////    System.out.println(Instant.now());
////    System.out.println(LocalDateTime.now());
////    System.out.println(OffsetDateTime.now());
////    System.out.println(ZonedDateTime.now());
////
////    System.out.println();
//
//    System.out.println(Instant.now(clock));
//////    System.out.println(Instant.now(clock).toEpochMilli());
//////    System.out.println(LocalDateTime.now(clock));
//////    System.out.println(OffsetDateTime.now(clock));
//    ZonedDateTime zonedDateTime = ZonedDateTime.now(clock);
//
//    System.out.println(zonedDateTime);
//    System.out.println(zonedDateTime.toLocalDateTime());
//    System.out.println(zonedDateTime.toLocalDateTime().toInstant(ZoneOffset.UTC));
//    System.out.println(zonedDateTime.toLocalDateTime().toInstant(
//      ZoneId.of("Europe/Istanbul").getRules().getOffset(Instant.now(clock))
//    ));
//    System.out.println(zonedDateTime.toInstant());

//    System.out.println(ZonedDateTime.now(clock));
////    System.out.println(ZonedDateTime.now(clock).toInstant().toEpochMilli());
//    System.out.println(TimeUnit.SECONDS.toNanos(ZonedDateTime.now(clock).toInstant().getEpochSecond()));
//    System.out.println(TimeUnit.NANOSECONDS.toNanos(ZonedDateTime.now(clock).toInstant().getNano()));
//    System.out.println(TimeUnit.SECONDS.toNanos(ZonedDateTime.now(clock).toInstant().getEpochSecond()) + TimeUnit.NANOSECONDS.toNanos(ZonedDateTime.now(clock).toInstant().getNano()));

//    int nano = ZonedDateTime.now(clock).toInstant().getNano();
//    System.out.println(nano);
//    System.out.println((nano / 1_000_000) * 1_000_000);
//    System.out.println((nano - (nano / 1_000_000) * 1_000_000) * 1_000);
//
//    long result = TimeUnit.MILLISECONDS.toNanos(ZonedDateTime.now(clock).toInstant().toEpochMilli()) + (nano - (nano / 1_000_000) * 1_000_000);
//
//    System.out.println(result);

//    Instant.ofEpochSecond(
//      TimeUnit.MICROSECONDS.toSeconds(microsSinceEpoch),
//      TimeUnit.MICROSECONDS.toNanos(
//        Math.floorMod(microsSinceEpoch, TimeUnit.SECONDS.toMicros(1))
//      )
//    );
  }

  @Test
  void testSimpleMapper() {
    TestHelper.withMongoClient(mongoClient -> {

      SimpleMapper simpleMapper = SimpleMapper.builder()
                                              .forPackage("tr.com.hive.smm.model")
//                                              .forClass(EmbeddedA1.class)
//                                              .forClass(ClassB2.class)
                                              .build();

      CodecRegistry codecRegistry = simpleMapper.getCodecRegistry();

      MongoDatabase database = mongoClient.getDatabase("sample")
                                          .withCodecRegistry(codecRegistry);

      MongoCollection<ClassA1> collection = database.getCollection(ClassA1.class.getSimpleName(), ClassA1.class);

      Clock clock = Clock.fixed(Instant.parse("2024-04-17T07:24:46.878915Z"), ZoneId.of("Europe/Istanbul"));

      ClassA1 classA1 = new ClassA1();

      ObjectId id = new ObjectId();
      classA1.id = id;
      classA1.varString = "myVal";
      classA1.varString2 = "myVal2";
      classA1.varNullString = null;
      classA1.varEnum = MyEnum.En1;
      classA1.varInt = 1;
      classA1.varBoxedInt = 2;

      ObjectId varId = new ObjectId();
      classA1.varObjectId = varId;

      Date now = new Date();
      classA1.varDate = now;
      classA1.varInstant = Instant.now(clock);

      classA1.varStringTransient = "asdasdasd";

      ObjectId classBId2 = new ObjectId();
      ClassB classB2 = new ClassB(classBId2);
      classB2.varString = "str";
      classB2.varInt = 3;
      Date now2 = new Date();
      classB2.varDate = now2;
      classB2.varEnum = MyEnum.En2;
//
      classA1.varClassB = classB2;

      classA1.varClassB2 = ClassB2.create(id);

      classA1.varStringSuper = "myVal";
      classA1.setVarIntSuper(1);

      BigDecimal varBigDecimal = new BigDecimal(13);
      classA1.varBigDecimal = varBigDecimal;
      BigInteger varBigInteger = BigInteger.valueOf(1);
      classA1.varBigInteger = varBigInteger;

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
      classA1.varListOfClassB = List.of(ClassB.create(id1, 1), ClassB.create(id2, 2));
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

      // java.time
      classA1.varZonedDateTime = ZonedDateTime.now(clock);
      classA1.varYearMonth = YearMonth.now(clock);
      classA1.varYear = Year.now(clock);
      Duration dur1 = Duration.ofDays(455);
      classA1.varDuration = dur1;
      Duration dur2 = Duration.ofSeconds(100, 99L);
      classA1.varDuration2 = dur2;

      // mongorefs
      ObjectId classBId = new ObjectId();
      ClassBRef classB = new ClassBRef(classBId);
      classB.varString = "str";
      classA1.refClassB = classB;
      classA1.setRefClassBPrivate(classB);

      ObjectId id5 = new ObjectId();
      ObjectId id6 = new ObjectId();
      classA1.refListOfClassA = List.of(
        ClassA1.create(id5, "ss"),
        ClassA1.create(id6, "s1")
      );

      collection.insertOne(classA1);

      Document document = database.getCollection(ClassA1.class.getSimpleName())
                                  .find().first();

      Objects.requireNonNull(document);
      assertNotNull(document.getObjectId("_id"));
      assertNotNull(document.get("varClassB", Document.class).get("id"));
      assertNull(document.getString("varString2"));
      assertNotNull(document.getString("varStr"));

      // java.time
      assertNotNull(document.get("varDuration", Document.class));
      assertNotNull(document.get("varDuration", Document.class).get("seconds"));
      assertEquals(39312000L, document.get("varDuration", Document.class).get("seconds"));
      assertEquals(0, document.get("varDuration", Document.class).get("nanos"));
      assertEquals(Decimal128.parse("39312000.000000000"), document.get("varDuration", Document.class).get("value"));

      assertNotNull(document.get("varDuration2", Document.class));
      assertNotNull(document.get("varDuration2", Document.class).get("seconds"));
      assertEquals(100L, document.get("varDuration2", Document.class).get("seconds"));
      assertEquals(99, document.get("varDuration2", Document.class).get("nanos"));
      assertEquals(Decimal128.parse("100.000000099"), document.get("varDuration2", Document.class).get("value"));

      ClassA1 fromDb = collection.find(Filters.eq("_id", classA1.id)).first();

      Objects.requireNonNull(fromDb);

      assertEquals(new ObjectId(id.toString()), fromDb.id);
      assertEquals("myVal", fromDb.varString);
      assertEquals("myVal2", fromDb.varString2);
      assertNull(fromDb.varNullString);
      assertEquals(MyEnum.En1, fromDb.varEnum);
      assertEquals(1, fromDb.varInt);
      assertEquals(2, fromDb.varBoxedInt);
      assertEquals(new ObjectId(varId.toString()), fromDb.varObjectId);
      assertEquals(new Date(now.getTime()), fromDb.varDate);
      assertEquals(Instant.ofEpochMilli(Instant.now(clock).toEpochMilli()), fromDb.varInstant); // we are storing instant as BSONDate, so we are losing precision (nano).
      assertNull(fromDb.varStringTransient);

      assertNotNull(fromDb.varClassB);
      assertNotNull(fromDb.varClassB.id);
      assertNotNull(fromDb.varClassB.varString);
      assertEquals(new ObjectId(classBId2.toString()), fromDb.varClassB.id);
      assertEquals("str", fromDb.varClassB.varString);
      assertEquals(new Date(now2.getTime()), fromDb.varClassB.varDate);
      assertEquals(MyEnum.En2, fromDb.varClassB.varEnum);
      assertEquals(id, fromDb.varClassB2._id);

      assertEquals("myVal", fromDb.varStringSuper);
      assertEquals(1, fromDb.getVarIntSuper());

      assertEquals(new BigDecimal(varBigDecimal.toBigInteger()), fromDb.varBigDecimal);
      assertEquals(varBigInteger, fromDb.varBigInteger);

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
      assertEquals(id1, fromDb.varListOfClassB.getFirst().id);
      assertNotEquals(id1, fromDb.varListOfClassB.get(1).id);
      assertEquals(id2, fromDb.varListOfClassB.get(1).id);
      assertNull(fromDb.varListOfClassB.get(1).varTransientList);
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
      assertNotNull(fromDb.getRefClassBPrivate());
      assertNotNull(fromDb.getRefClassBPrivate().id);
      assertNull(fromDb.getRefClassBPrivate().varString);
      assertEquals(new ObjectId(classBId.toString()), fromDb.getRefClassBPrivate().id);

      assertEquals(2, fromDb.refListOfClassA.size());
      assertEquals(id5, fromDb.refListOfClassA.getFirst().id);
      assertNull(fromDb.refListOfClassA.getFirst().varString);

      assertNotNull(fromDb.refListOfClassBEmptyWithInit);

      // java.time
      assertNotNull(fromDb.varZonedDateTime);
      assertEquals(ZonedDateTime.now(clock), fromDb.varZonedDateTime);
      assertNotNull(fromDb.varYearMonth);
      assertEquals(YearMonth.now(clock), fromDb.varYearMonth);
      assertNotNull(fromDb.varYear);
      assertEquals(Year.now(clock), fromDb.varYear);

      // custom converter
      assertEquals(2, fromDb.varNestedMapWithCustomCodec.size());
      assertEquals(1, fromDb.varNestedMapWithCustomCodec.get("k1").getFirst().size());
      assertNotNull(fromDb.varNestedMapWithCustomCodec.get("k1").getFirst().get("k11" + 123));

    });
  }

}