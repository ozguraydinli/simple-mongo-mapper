package tr.com.hive.smm;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.mongodb.DBRef;

import org.bson.*;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import tr.com.hive.smm.mapping.Converter;
import tr.com.hive.smm.mapping.MappingException;
import tr.com.hive.smm.mapping.annotation.MongoCustomConverter;
import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.mapping.annotation.MongoId;
import tr.com.hive.smm.model.*;

/**
 * Created by ozgur on 4/4/17.
 *
 * Simluates as if a query is returned as a Document
 */
public class SimpleMongoMapperTest {

  @Test
  public void testSingleField() {
    Document document = new Document();
    ObjectId id = new ObjectId();

    document.put("_id", id);
    document.put("varString", "myVal");
    document.put("varNullString", null);
    document.put("varEnum", MyEnum.En1.name());
    document.put("varInt", Integer.valueOf(1));
    document.put("varBoxedInt", Integer.valueOf(1));

    ObjectId varId = new ObjectId();
    document.put("varObjectId", varId);

    Date now = new Date();
    document.put("varDate", now);

    ObjectId classBId = new ObjectId();
    document.put("refClassB", new DBRef(ClassB.class.getSimpleName(), classBId));

    document.put("varClassB", ClassB.createDocument(1, now));

    document.put("varBsonUndefined", new BsonUndefined());

    SimpleMongoMapper simpleMongoMapper = new SimpleMongoMapper();
    ClassA classA = simpleMongoMapper.fromDocument(document, ClassA.class);

    Assert.assertEquals(new ObjectId(id.toString()), classA.id);
    Assert.assertEquals("myVal", classA.varString);
    Assert.assertNull(classA.varNullString);
    Assert.assertEquals(MyEnum.En1, classA.varEnum);
    Assert.assertEquals(1, classA.varInt);
    Assert.assertEquals(Integer.valueOf(1), classA.varBoxedInt);
    Assert.assertEquals(new Date(now.getTime()), classA.varDate);
    Assert.assertEquals(new ObjectId(varId.toString()), classA.varObjectId);
    Assert.assertEquals(new ObjectId(classBId.toString()), classA.refClassB.id);
    Assert.assertEquals("s1", classA.varClassB.varString);
    Assert.assertEquals(1, classA.varClassB.varInt);
    Assert.assertEquals(new Date(now.getTime()), classA.varClassB.varDate);
    Assert.assertEquals(MyEnum.En2, classA.varClassB.varEnum);
    Assert.assertNull(classA.varClassB2);
    Assert.assertNull(classA.varClazzB2);
    Assert.assertNull(classA.varBsonUndefined);
  }

  @Test
  public void testSingleField_SuperClass() {
    Document document = new Document();
    document.put("varStringSuper", "myVal");
    document.put("varIntSuper", 1);

    SimpleMongoMapper simpleMongoMapper = new SimpleMongoMapper();
    ClassA classA = simpleMongoMapper.fromDocument(document, ClassA.class);

    Assert.assertEquals("myVal", classA.varStringSuper);
//    Assert.assertEquals(1, classA.varIntSuper);
  }

  @Test
  public void testSingleField_BigDecimal() {
    Document document = new Document();

    BigDecimal varBigDecimal = new BigDecimal(13);
    document.put("varBigDecimal", new Decimal128(varBigDecimal));

    SimpleMongoMapper simpleMongoMapper = new SimpleMongoMapper();
    ClassA classA = simpleMongoMapper.fromDocument(document, ClassA.class);

    Assert.assertEquals(new BigDecimal(varBigDecimal.toBigInteger()), classA.varBigDecimal);
  }

  @Test
  public void testCollectionField() {
    Document document = new Document();
    ObjectId id = new ObjectId();

    document.put("_id", id);
    document.put("varListOfString", Lists.newArrayList("a1", "a2", "a3"));
    document.put("varListOfInteger1", Lists.newArrayList(1, 2));
    document.put("varListOfInteger2", Lists.newArrayList(Integer.valueOf(3), Integer.valueOf(4)));
    document.put("varListOfDate", Lists.newArrayList(new Date()));
    document.put("varListOfEnum", Lists.newArrayList(MyEnum.En1.name(), MyEnum.En2.name()));
    document.put("varSetOfEnum", Lists.newArrayList(MyEnum.En1.name(), MyEnum.En2.name()));
    document.put("varSetOfString", Lists.newArrayList("a1", "a2", "a3"));

    ObjectId id1 = new ObjectId();
    ObjectId id2 = new ObjectId();
    document.put("varListOfObjectId", Lists.newArrayList(id1, id2));

    document.put("varListOfClassB", Lists.newArrayList(ClassB.createDocument(1, new Date()), ClassB.createDocument(2, new Date())));

    ObjectId classBId = new ObjectId();
    document.put("refListOfClassB", Lists.newArrayList(
      new DBRef(ClassB.class.getSimpleName(), classBId), new DBRef(ClassB.class.getSimpleName(), new ObjectId()),
      new DBRef(ClassB.class.getSimpleName(), new ObjectId()), new DBRef(ClassB.class.getSimpleName(), new ObjectId())));

    document.put("varListOfListOfString", Lists.newArrayList(
      Lists.newArrayList("a11", "a12", "a13"), Lists.newArrayList("a21", "a22", "a23")));

    ObjectId id11 = new ObjectId();
    document.put("varListOfListOfObjectId", Lists.newArrayList(
      Lists.newArrayList(id11, new ObjectId()), Lists.newArrayList(new ObjectId())));

    document.put("varListOfListOfClassB", Lists.newArrayList(
      Lists.newArrayList(ClassB.createDocument(1, new Date())),
      Lists.newArrayList(ClassB.createDocument(3, new Date()), ClassB.createDocument(4, new Date()))));

    SimpleMongoMapper simpleMongoMapper = new SimpleMongoMapper();
    ClassA classA = simpleMongoMapper.fromDocument(document, ClassA.class);

    Assert.assertEquals(new ObjectId(id.toString()), classA.id);
    Assert.assertEquals(3, classA.varListOfString.size());
    Assert.assertEquals("a1", classA.varListOfString.get(0));
    Assert.assertEquals(2, classA.varListOfInteger1.size());
    Assert.assertEquals(Integer.valueOf(1), classA.varListOfInteger1.get(0));
    Assert.assertEquals(2, classA.varListOfInteger2.size());
    Assert.assertEquals(Integer.valueOf(3), classA.varListOfInteger2.get(0));
    Assert.assertEquals(1, classA.varListOfDate.size());
    Assert.assertEquals(2, classA.varListOfEnum.size());
    Assert.assertEquals(MyEnum.En1, classA.varListOfEnum.get(0));
    Assert.assertEquals(2, classA.varListOfObjectId.size());
    Assert.assertEquals(new ObjectId(id1.toString()), classA.varListOfObjectId.get(0));
    Assert.assertEquals(2, classA.varListOfClassB.size());
    Assert.assertEquals("s1", classA.varListOfClassB.get(0).varString);
    Assert.assertEquals(MyEnum.En2, classA.varListOfClassB.get(0).varEnum);
    Assert.assertEquals(2, classA.varListOfClassB.get(0).varListOfString.size());
    Assert.assertEquals("b1", classA.varListOfClassB.get(0).varListOfString.get(0));
    Assert.assertEquals(2, classA.varListOfListOfString.size());
    Assert.assertEquals(3, classA.varListOfListOfString.get(0).size());
    Assert.assertEquals("a11", classA.varListOfListOfString.get(0).get(0));
    Assert.assertEquals(2, classA.varListOfListOfObjectId.size());
    Assert.assertEquals(2, classA.varListOfListOfObjectId.get(0).size());
    Assert.assertEquals(new ObjectId(id11.toString()), classA.varListOfListOfObjectId.get(0).get(0));
    Assert.assertEquals(2, classA.varListOfListOfClassB.size());
    Assert.assertEquals(1, classA.varListOfListOfClassB.get(0).size());
    Assert.assertEquals("s1", classA.varListOfListOfClassB.get(0).get(0).varString);
    Assert.assertEquals(4, classA.refListOfClassB.size());
    Assert.assertEquals(new ObjectId(classBId.toString()), classA.refListOfClassB.get(0).id);

    Assert.assertEquals(null, classA.refListOfClassBEmpty);

    Assert.assertEquals(2, classA.varSetOfEnum.size());
    Assert.assertEquals(true, classA.varSetOfEnum.contains(MyEnum.En1));

    Assert.assertEquals(3, classA.varSetOfString.size());
    Assert.assertEquals(true, classA.varSetOfString.contains("a1"));
  }

  @Test
  public void testMapField() {
    Document document = new Document();
    ObjectId id = new ObjectId();

    document.put("_id", id);

    document.put("varMapOfString", createDocumentForMapString());
    document.put("varMapOfEnumString", createDocumentForMapEnumString());
    document.put("varMapOfIntegerString", createDocumentForMapIntegerString());
    ObjectId id1 = new ObjectId();
    ObjectId id2 = new ObjectId();
    document.put("varMapOfObjectIdString", createDocumentForMapObjectIdString(id1, id2));
    document.put("varMapOfObjectId", createDocumentForMapObjectId(id1));
    Date now = new Date();
    document.put("varMapOfDate", createDocumentForMapDate(now));

    Document documentForMapClassB = createDocumentForMapClassB();
    document.put("varMapOfClassB", documentForMapClassB);

    document.put("varMapOfListOfString", createDocumentForMapListOfString());
    document.put("varMapOfListOfDate", createDocumentForMapListOfDate(now));
    document.put("varMapOfListOfClassB", createDocumentForMapListOfClassB());
    document.put("varMapOfMapOfString", createDocumentForMapOfMapOfString());

    SimpleMongoMapper simpleMongoMapper = new SimpleMongoMapper();
    ClassA classA = simpleMongoMapper.fromDocument(document, ClassA.class);

    Assert.assertEquals(2, classA.varMapOfString.size());
    Assert.assertEquals("1", classA.varMapOfString.get("a"));

    Assert.assertEquals(2, classA.varMapOfEnumString.size());
    Assert.assertEquals("1", classA.varMapOfEnumString.get(MyEnum.En1));

    Assert.assertEquals(2, classA.varMapOfIntegerString.size());
    Assert.assertEquals("1", classA.varMapOfIntegerString.get(1));

    Assert.assertEquals(2, classA.varMapOfObjectIdString.size());
    Assert.assertEquals("1", classA.varMapOfObjectIdString.get(id1));
    Assert.assertEquals(2, classA.varMapOfObjectId.size());
    Assert.assertEquals(new ObjectId(id1.toString()), classA.varMapOfObjectId.get("a"));

    Assert.assertEquals(2, classA.varMapOfDate.size());
    Assert.assertEquals(new Date(now.getTime()), classA.varMapOfDate.get("a"));

    Assert.assertEquals(2, classA.varMapOfClassB.size());
    Assert.assertEquals("s1", classA.varMapOfClassB.get("a").varString);
    Assert.assertEquals(2, classA.varMapOfClassB.get("a").varListOfString.size());
    Assert.assertEquals("b1", classA.varMapOfClassB.get("a").varListOfString.get(0));
    Assert.assertNotNull(classA.varMapOfClassB.get("a").id);

    Assert.assertEquals(2, classA.varMapOfListOfString.size());
    Assert.assertEquals("a1", classA.varMapOfListOfString.get("a").get(0));

    Assert.assertEquals(2, classA.varMapOfListOfDate.size());
    Assert.assertEquals(new Date(now.getTime()), classA.varMapOfListOfDate.get("a").get(0));

    Assert.assertEquals(2, classA.varMapOfListOfClassB.size());
    Assert.assertEquals("s1", classA.varMapOfListOfClassB.get("a").get(0).varString);

    Assert.assertEquals(2, classA.varMapOfMapOfString.size());
    Assert.assertEquals("1", classA.varMapOfMapOfString.get("a").get("a"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void test_toDocument() {
    ClassA classA = new ClassA();
    ObjectId id = new ObjectId();
    classA.id = id;
    ObjectId classBId = new ObjectId();
    classA.refClassB = new ClassB(classBId);
    classA.varString = "s1";
    classA.varNullString = null;
    classA.varInt = 1;
    classA.varBoxedInt = Integer.valueOf(1);
    classA.varEnum = MyEnum.En1;
    classA.varDate = new Date();

    ObjectId id2 = new ObjectId();
    classA.varClassC = new ClassC(id2);
    classA.varClassB = ClassB.create(1);
    classA.varMapOfString = Maps.newHashMap();
    classA.varMapOfString.put("a", "1");
    classA.varMapOfString.put("b", "2");
    classA.varMapOfEnumString = Maps.newHashMap();
    classA.varMapOfEnumString.put(MyEnum.En1, "1");
    classA.varMapOfEnumString.put(MyEnum.En2, "2");
    classA.varMapOfString2 = Maps.newHashMap();

    SimpleMongoMapper simpleMongoMapper = new SimpleMongoMapper();
    Document document = simpleMongoMapper.toDocument(classA);

    Assert.assertEquals(new ObjectId(id.toString()), document.getObjectId("_id"));
    Assert.assertNull(document.get("id"));

    Assert.assertEquals("s1", document.getString("varString"));
    Assert.assertNull(document.get("varNullString"));

    Assert.assertEquals(Integer.valueOf(1), document.getInteger("varInt"));
    Assert.assertEquals(Integer.valueOf(1), document.getInteger("varBoxedInt"));
    Assert.assertEquals("En1", document.getString("varEnum"));
    Assert.assertEquals(new Date(classA.varDate.getTime()), document.getDate("varDate"));
    Assert.assertEquals("s1", document(document.get("varClassB")).getString("varString"));
    Assert.assertEquals(new ObjectId(id2.toString()), document(document.get("varClassC")).getObjectId("Id"));
    Assert.assertEquals(Integer.valueOf(1), document(document.get("varClassB")).getInteger("varInt"));
    Assert.assertEquals(new Date(classA.varClassB.varDate.getTime()), document(document.get("varClassB")).getDate("varDate"));
    Assert.assertEquals("En2", document(document.get("varClassB")).getString("varEnum"));
    Assert.assertEquals(2, ((ArrayList<String>) document(document.get("varClassB")).get("varListOfString")).size());
    Assert.assertEquals("bb1", ((ArrayList<String>) document(document.get("varClassB")).get("varListOfString")).get(1));
    Assert.assertEquals("1", document(document.get("varMapOfString")).get("a"));
    Assert.assertEquals("1", document(document.get("varMapOfEnumString")).get(MyEnum.En1.name()));
    Assert.assertNull(document.get("varClassB2"));
    Assert.assertNull(document.get("varMapOfString2"));
  }

  @Test
  public void test_toDocument_BigDecimal() {
    ClassA classA = new ClassA();
    classA.varBigDecimal = new BigDecimal(15);

    SimpleMongoMapper simpleMongoMapper = new SimpleMongoMapper();
    Document document = simpleMongoMapper.toDocument(classA);

    Assert.assertEquals(new BigDecimal(classA.varBigDecimal.toBigInteger()), ((Decimal128) document.get("varBigDecimal")).bigDecimalValue());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void test_toDocument_EmptyCollection() {
    ClassA classA = new ClassA();
    classA.varListOfClassB_MongoField = null;

    SimpleMongoMapper simpleMongoMapper = new SimpleMongoMapper();
    Document document = simpleMongoMapper.toDocument(classA);

    Assert.assertEquals(null, document.get("varListOfClassB_MongoField"));

    classA.varListOfClassB_MongoField = Lists.newArrayList();
    document = simpleMongoMapper.toDocument(classA);
    Assert.assertEquals(0, ((ArrayList<Document>) document.get("varListOfClassB_MongoField")).size());

    ObjectId id = new ObjectId();
    classA.varListOfClassB_MongoField = Lists.newArrayList(new ClassB(id));
    document = simpleMongoMapper.toDocument(classA);
    Assert.assertEquals(id, ((ArrayList<Document>) document.get("varListOfClassB_MongoField")).get(0).getObjectId("id"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void test_toBsonValue() {
    ClassA classA = new ClassA();
    classA.varString = "s1";

    classA.varMapOfClassB = Maps.newHashMap();
    ObjectId idOfClassB = new ObjectId();
    classA.varMapOfClassB.put("a", ClassB.create(idOfClassB, 1));

    classA.varDate = new Date();
    classA.varComplexEnum = MyComplexEnum.ComplexEn1;
    classA.varListOfComplexEnum = Lists.newArrayList(MyComplexEnum.ComplexEn1, MyComplexEnum.ComplexEn2);

    SimpleMongoMapper simpleMongoMapper = new SimpleMongoMapper();
    BsonValue bsonValue = simpleMongoMapper.toBsonValue(classA);

    BsonDocument document = bsonValue.asDocument();

    Assert.assertEquals("s1", document.getString("varString").getValue());
    Assert.assertEquals(classA.varDate.getTime(), document.getDateTime("varDate").getValue());
    Assert.assertEquals(MyComplexEnum.ComplexEn1, MyComplexEnum.valueOf(document.getString("varComplexEnum").getValue()));
    Assert.assertEquals(MyComplexEnum.ComplexEn2, MyComplexEnum.valueOf(document.getArray("varListOfComplexEnum").get(1).asString().getValue()));

    Assert.assertEquals(idOfClassB, document.get("varMapOfClassB").asDocument().get("a").asDocument().getObjectId("id").getValue());

    Assert.assertEquals(MyComplexEnum.ComplexEn1.name(), simpleMongoMapper.toBsonValue(MyComplexEnum.ComplexEn1).asString().getValue());
  }

  @Test
  public void test_PrivateConstructor() {
    ObjectId id = new ObjectId();
    Document document = new Document("_id", id).append("stringField", "field value");

    SimpleMongoMapper simpleMongoMapper = new SimpleMongoMapper();
    PrivateConsClazz clazz = simpleMongoMapper.fromDocument(document, PrivateConsClazz.class);

    Assert.assertEquals(id, clazz.getId());
    Assert.assertEquals("field value", clazz.getStringField());
  }

  @Test
  public void test_CustomConverter() {
    ObjectId id = new ObjectId();
    Document document = new Document("_id", id).append("stringField", "field value");

    SimpleMongoMapper simpleMongoMapper = new SimpleMongoMapper();
    CustomConverterClazz clazz = simpleMongoMapper.fromDocument(document, CustomConverterClazz.class);

    Assert.assertEquals(id, clazz.id);
    Assert.assertEquals("field valuea", clazz.stringField);
  }

  @Test
  public void test_RefField_encodeToDocument() {
    ClassA classA = new ClassA();
    classA.id = new ObjectId();

    ObjectId classBId = new ObjectId();
    classA.refClassB = new ClassB(classBId);

    SimpleMongoMapper simpleMongoMapper = new SimpleMongoMapper();
    Document document = simpleMongoMapper.toDocument(classA);

    Assert.assertEquals(ClassB.class.getSimpleName(), ((DBRef) document.get("refClassB")).getCollectionName());
    Assert.assertEquals(classBId, ((DBRef) document.get("refClassB")).getId());
  }

  @Test
  public void test_CollectionRefField_encodeToDocument() {
    ClassA classA = new ClassA();
    classA.id = new ObjectId();

    ObjectId innerId = new ObjectId();
    classA.refListOfClassA = Lists.newArrayList(new ClassA(innerId));

    SimpleMongoMapper simpleMongoMapper = new SimpleMongoMapper();
    Document document = simpleMongoMapper.toDocument(classA);

    Assert.assertEquals(1, ((ArrayList<DBRef>) document.get("refListOfClassA")).size());
    Assert.assertEquals(innerId, ((ArrayList<DBRef>) document.get("refListOfClassA")).get(0).getId());
//    Assert.assertEquals("field valuea", clazz.stringField);
  }


  @Test
  public void test_mappingOfFieldName_id() {
    Document document = new Document();
    document.put("_id", new ObjectId());

    Document innerDocument = new Document();
    ObjectId innerIdValue = new ObjectId();
    innerDocument.put("_id", innerIdValue);

    document.put("varClazzB2", innerDocument);

    SimpleMongoMapper simpleMongoMapper = new SimpleMongoMapper();
    ClassA classA = simpleMongoMapper.fromDocument(document, ClassA.class);

    Assert.assertNotNull(classA.varClazzB2._id);
  }

  @Test
  public void test_BoxedPrimitiveMapping_nullField() {
    Document document = new Document();
    document.put("_id", new ObjectId());
    document.put("varBoxedInt", null);
    document.put("varBoxedBoolean", BsonNull.VALUE);

    SimpleMongoMapper simpleMongoMapper = new SimpleMongoMapper();
    ClassA classA = simpleMongoMapper.fromDocument(document, ClassA.class);

    Assert.assertNull(classA.varBoxedInt);
    Assert.assertNull(classA.varBoxedBoolean);
  }

  protected static Document document(Object obj) {
    return (Document) obj;
  }

  private Document createDocumentForMapString() {
    Document document = new Document();

    document.put("a", "1");
    document.put("b", "2");

    return document;
  }

  private Document createDocumentForMapEnumString() {
    Document document = new Document();

    document.put(MyEnum.En1.name(), "1");
    document.put(MyEnum.En2.name(), "2");

    return document;
  }

  private Document createDocumentForMapIntegerString() {
    Document document = new Document();

    document.put("1", "1");
    document.put("2", "2");

    return document;
  }

  private Document createDocumentForMapObjectIdString(ObjectId id1, ObjectId id2) {
    Document document = new Document();

    document.put(id1.toString(), "1");
    document.put(id2.toString(), "2");

    return document;
  }

  private Document createDocumentForMapObjectId(ObjectId id) {
    Document document = new Document();

    document.put("a", id);
    document.put("b", new ObjectId());

    return document;
  }

  private Document createDocumentForMapDate(Date date) {
    Document document = new Document();

    document.put("a", date);
    document.put("b", new Date());

    return document;
  }

  private Document createDocumentForMapClassB() {
    Document document = new Document();

    document.put("a", ClassB.createDocument(1, new Date()));
    document.put("b", ClassB.createDocument(2, new Date()));

    return document;
  }

  private Document createDocumentForMapListOfString() {
    Document document = new Document();

    document.put("a", Lists.newArrayList("a1", "a2", "a3"));
    document.put("b", Lists.newArrayList("b1", "b2", "b3"));

    return document;
  }

  private Document createDocumentForMapListOfDate(Date now) {
    Document document = new Document();

    document.put("a", Lists.newArrayList(now, new Date()));
    document.put("b", Lists.newArrayList(new Date(), new Date()));

    return document;
  }

  private Document createDocumentForMapListOfClassB() {
    Document document = new Document();

    document.put("a", Lists.newArrayList(
      ClassB.createDocument(1, new Date()),
      ClassB.createDocument(2, new Date())
    ));

    document.put("b", Lists.newArrayList(
      ClassB.createDocument(3, new Date()),
      ClassB.createDocument(4, new Date())
    ));

    return document;
  }

  private Document createDocumentForMapOfMapOfString() {
    Document document = new Document();

    Document document1 = new Document();
    document1.put("a", "1");
    document1.put("b", "2");

    document.put("a", document1);

    Document document2 = new Document();
    document2.put("a", "1");
    document2.put("b", "2");
    document.put("b", document2);

    return document;
  }

  @MongoEntity
  private static class CustomConverterClazz {

    @MongoId
    public ObjectId id;

    @MongoCustomConverter(CustomConverter.class)
    public String stringField;

  }

  private static class CustomConverter implements Converter {

    @Override
    public BsonValue encode(Object obj) throws MappingException {
      return null;
    }

    @Override
    public Object decode(Object obj) throws MappingException {
      return obj + "a";
    }

    @Override
    public Object encodeToDocument(Object obj) throws MappingException {
      return null;
    }

  }

}
