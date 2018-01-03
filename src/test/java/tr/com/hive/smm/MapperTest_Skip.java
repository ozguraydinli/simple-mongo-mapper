package tr.com.hive.smm;

/**
 * Created by ozgur on 4/7/17.
 */

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import com.mongodb.*;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.*;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.List;

/**
 * Created by ozgur on 3/24/16.
 */
public class MapperTest_Skip {

  private static MongoClient MONGO_CLIENT;

  @Test
  public void test_map() throws Exception {
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

  public static MongoDatabase getDatabase(String dbName) {
    if (MONGO_CLIENT == null) {
      MONGO_CLIENT = getMongoClient();
    }

    return MONGO_CLIENT.getDatabase(dbName);
  }

  public static MongoClient getMongoClient() {

    if (MONGO_CLIENT == null) {
      try {

        MONGO_CLIENT = getMongoClient("127.0.0.1", "27017", "", "");

      } catch (NumberFormatException | MongoException e) {
//        System.err.println(e.toString() + " $ " +  getSimpleName() + " $ getMongoClient $ " + e.getMessage());
      }
    }

    return MONGO_CLIENT;
  }

  private static MongoClient getMongoClient(String host, String port, String dbUsername, String dbPassword)
    throws NumberFormatException, MongoException {

    MongoClientOptions options = new Builder()
      .writeConcern(WriteConcern.ACKNOWLEDGED)
      .build();

    if (!Strings.isNullOrEmpty(dbUsername) && !Strings.isNullOrEmpty(dbPassword)) {

      List<MongoCredential> credentialsList = Lists.newArrayList(MongoCredential.createCredential(dbUsername, "admin", dbPassword.toCharArray()));
      List<String> split = Lists.newArrayList(
        Splitter.on(",").omitEmptyStrings().limit(3).trimResults().split(host));

      if (split.size() > 1) {
        List<ServerAddress> serverAddresses = Lists.transform(split, input -> new ServerAddress(input, Integer.valueOf(port)));

        return new MongoClient(serverAddresses, credentialsList, options);
      }

      return new MongoClient(new ServerAddress(host, Integer.valueOf(port)), credentialsList, options);

    } else {
      return new MongoClient(new ServerAddress(host, Integer.valueOf(port)), options);
    }
  }

}
