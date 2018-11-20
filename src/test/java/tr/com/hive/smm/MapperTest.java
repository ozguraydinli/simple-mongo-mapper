package tr.com.hive.smm;

/**
 * Created by ozgur on 4/7/17.
 */

import com.google.common.collect.Lists;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

import junit.framework.TestCase;

import org.bson.*;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.mapping.annotation.MongoId;
import tr.com.hive.smm.mapping.annotation.index.Field;
import tr.com.hive.smm.mapping.annotation.index.Index;
import tr.com.hive.smm.mapping.annotation.index.IndexOptions;
import tr.com.hive.smm.mapping.annotation.index.Indexes;

/**
 * Created by ozgur on 3/24/16.
 */
public class MapperTest extends TestCase {

  private static final MongodStarter starter = MongodStarter.getDefaultInstance();

  private MongodExecutable _mongodExe;
  private MongodProcess _mongod;

  private MongoClient _mongo;

  @Override
  protected void setUp() throws Exception {
    _mongodExe = starter.prepare(new MongodConfigBuilder()
                                   .version(Version.Main.PRODUCTION)
                                   .net(new Net("localhost", 12345, Network.localhostIsIPv6()))
                                   .build());
    _mongod = _mongodExe.start();

    super.setUp();

    _mongo = new MongoClient("localhost", 12345);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();

    _mongod.stop();
    _mongodExe.stop();
  }

  public MongoClient getMongo() {
    return _mongo;
  }

  @Test
  public void test_map() {
    BsonDocument document = new BsonDocument();
    ObjectId id = new ObjectId();
//    document.put("_id", id);
//    document.put("varInt", 1);
//    document.put("varDouble", 2.4);
//    document.put("varString", "asd");
//    document.put("myEnum", "En1");

//    BsonArray bsonStrings1 = createBsonArray(1);
//    BsonArray bsonStrings2 = createBsonArray(2);
//
//    BsonArray bsonStrings3 = createBsonArray(3);
//    BsonArray bsonStrings4 = createBsonArray(5);

    BsonArray bsonStrings1 = createBsonArrayClassB(1);
    BsonArray bsonStrings2 = createBsonArrayClassB(3);

    BsonArray bsonStrings3 = createBsonArrayClassB(5);
    BsonArray bsonStrings4 = createBsonArrayClassB(7);

//    document.put("varListOfString", Lists.newArrayList(1, 2));
//    document.put("varListOfString", bsonStrings1);
    BsonArray bsonNestedList1 = new BsonArray();
    bsonNestedList1.add(bsonStrings1);
    bsonNestedList1.add(bsonStrings2);

    BsonArray bsonNestedList2 = new BsonArray();
    bsonNestedList2.add(bsonStrings3);
    bsonNestedList2.add(bsonStrings4);

    BsonArray bsonNestedList = new BsonArray();
    bsonNestedList.add(bsonNestedList1);
    bsonNestedList.add(bsonNestedList2);

//    document.put("varNestedSetOfClassB", bsonNestedList);
//    document.put("varListOfClassB", bsonStrings1);

    BsonDocument bsonClassB = createClassB(1);

    document.put("varClassB", bsonClassB);

    BsonArray bsonValues = new BsonArray();
    bsonValues.add(createClassB(1));
    bsonValues.add(createClassB(2));
    bsonValues.add(createClassB(3));
//    document.put("varListOfClassB", bsonValues);

    BsonArray bsonValuesSet = new BsonArray();
    bsonValuesSet.add(new BsonInt32(11));
    bsonValuesSet.add(new BsonInt32(12));
    bsonValuesSet.add(new BsonInt32(13));
//    document.put("varSet", bsonValuesSet);

    BsonDocument bsonDocument = new BsonDocument();
    bsonDocument.put("s1", new BsonInt32(1));
    bsonDocument.put("s2", new BsonInt32(2));
//    document.put("varMap", bsonDocument);

//    document.put("id", new ObjectId());

    BsonArray bsonListId = new BsonArray();
    bsonListId.add(new BsonObjectId(new ObjectId()));
    bsonListId.add(new BsonObjectId(new ObjectId()));
    document.put("listOfIds", bsonListId);

    BsonArray bsonListOfListOfStrings = new BsonArray();
    BsonArray bsonListOfStrings1 = new BsonArray();
    bsonListOfStrings1.add(new BsonString("11"));
    bsonListOfStrings1.add(new BsonString("12"));
    bsonListOfListOfStrings.add(bsonListOfStrings1);
    BsonArray bsonListOfStrings2 = new BsonArray();
    bsonListOfStrings2.add(new BsonString("21"));
    bsonListOfStrings2.add(new BsonString("22"));
    bsonListOfListOfStrings.add(bsonListOfStrings2);
    document.put("listOfListOfStrings", bsonListOfListOfStrings);

    BsonArray bsonListOfListOfCalssB = new BsonArray();
    BsonArray bsonListOfListOfCalssB1 = new BsonArray();
    bsonListOfListOfCalssB1.add(createClassB(1));
    bsonListOfListOfCalssB1.add(createClassB(2));
    bsonListOfListOfCalssB.add(bsonListOfListOfCalssB1);
    BsonArray bsonListOfListOfCalssB2 = new BsonArray();
    bsonListOfListOfCalssB2.add(createClassB(3));
    bsonListOfListOfCalssB2.add(createClassB(4));
    bsonListOfListOfCalssB.add(bsonListOfListOfCalssB2);
    document.put("listOfListOfClassB", bsonListOfListOfCalssB);

    MongoDatabase database = getDatabase("smm");
    MongoCollection<Document> collection = database.getCollection("test");

//    collection.updateMany(Filters.exists(""), Updates.set("", ""));

//    collection.insertOne(document);

    Document first = collection.find(new Document("_id", id)).first();
    System.out.println(first);

//    ClassA test = Mapper.map(first, ClassA.class);

//    collection.deleteOne(new Document("_id", id));
//    System.out.println(test);

    database.drop();
  }

  @Test
  public void test_Index() {
    MongoDatabase database = getDatabase("smm");

    IndexClass indexClass = new IndexClass();
    indexClass.id = new ObjectId();
    indexClass.field1 = "falans";
    indexClass.fieldCmp1 = "a1";
    indexClass.fieldCmp2 = "a2";
    indexClass.fieldCmp3 = "a3";

    SimpleMongoMapper mapper = new SimpleMongoMapper(database);

    Document document = mapper.toDocument(indexClass);

    // this should trigger createIndex
    mapper.fromDocument(document, IndexClass.class);

    MongoCollection<Document> collection = database.getCollection(IndexClass.class.getSimpleName());

    ArrayList<Document> indexes = collection.listIndexes().into(Lists.newArrayList());

    assertTrue(indexes.size() != 0);
    assertEquals(4, indexes.size()); // we expect 1 more for id field

    Set<String> indexNmaes = indexes.stream()
                                    .map(index -> index.getString("name"))
                                    .collect(Collectors.toSet());

    assertTrue(indexNmaes.contains("field1Index"));
    assertTrue(indexNmaes.contains("uniqueIdIndex"));
    assertTrue(indexNmaes.contains("cmpIndex"));

    database.drop();
  }

  private BsonArray createBsonArray(int i) {
    BsonArray bsonStrings1 = new BsonArray();

    bsonStrings1.add(new BsonString("a1" + i));
    bsonStrings1.add(new BsonString("a2" + i));
    bsonStrings1.add(new BsonString("a3" + i));

    return bsonStrings1;
  }

  private BsonArray createBsonArrayClassB(int i) {
    BsonArray bsonStrings1 = new BsonArray();

    bsonStrings1.add(createClassB(i));
    bsonStrings1.add(createClassB(i + 1));

    return bsonStrings1;
  }

  private BsonDocument createClassB(int i) {
    BsonDocument bsonDocument = new BsonDocument();
    bsonDocument.append("varInt", new BsonInt32(i));
    bsonDocument.append("varString", new BsonString("b" + i));

    BsonDocument map = new BsonDocument();
    map.put("s1", new BsonString("str1"));
    map.put("s2", new BsonString("str2"));
    bsonDocument.append("varMap", map);

    return bsonDocument;
  }

  public MongoDatabase getDatabase(String dbName) {
    return getMongo().getDatabase(dbName);
  }

  @Indexes({
    @Index(
      fields = {@Field(value = "field1")},
      options = @IndexOptions(name = "field1Index")),
    @Index(
      fields = {@Field(value = "uniqueId")},
      options = @IndexOptions(name = "uniqueIdIndex", unique = true)),
    @Index(
      fields = {@Field(value = "fieldCmp1"), @Field(value = "fieldCmp2"), @Field(value = "fieldCmp3")},
      options = @IndexOptions(name = "cmpIndex"))
  })
  @MongoEntity
  public static class IndexClass {

    @MongoId
    public ObjectId id;

    private int uniqueId;

    private String field1;

    private String fieldCmp1;

    private String fieldCmp2;

    private String fieldCmp3;

    public IndexClass() {
    }
  }

}
