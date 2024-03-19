package tr.com.hive.smm.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tr.com.hive.smm.mapping.annotation.MongoCustomConverter;
import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.mapping.annotation.MongoField;
import tr.com.hive.smm.mapping.annotation.MongoId;
import tr.com.hive.smm.mapping.annotation.MongoRef;
import tr.com.hive.smm.mapping.annotation.MongoTransient;
import tr.com.hive.smm.mapping2.MyCodec;

@MongoEntity
public class ClassA1 extends ClassA1Super {

  public static ClassA1 create(ObjectId id, String s) {
    ClassA1 classA1 = new ClassA1();
    classA1.id = id;
    classA1.varString = s;
    return classA1;
  }

  @MongoId
  public ObjectId id;

  public String varString;

  @MongoField("varStr")
  public String varString2;

  public String varNullString;

  public MyEnum varEnum;

  public int varInt;

  public Integer varBoxedInt;

  public Date varDate;

  public Instant varInstant;

  public ObjectId varObjectId;

  public BigDecimal varBigDecimal;

  @MongoRef
  public ClassB refClassB;

  public ClassB varClassB;

  // collections
  public List<String> varListOfString;

  public List<Integer> varListOfInteger1;

  public List<Date> varListOfDate;

  public List<MyEnum> varListOfEnum;

  public List<MyComplexEnum> varListOfComplexEnum;

  public List<ObjectId> varListOfObjectId;

  public List<ClassB> varListOfClassB;

  public List<ClassB> varListOfClassB_Null;
  public List<ClassB> varListOfClassB_Empty = Lists.newArrayList();

  public Map<String, String> varMapOfString;

  public Map<String, String> varMapOfString2;

  public Map<MyEnum, String> varMapOfEnumString;

  public Map<ObjectId, String> varMapOfObjectIdString;

  public Map<Integer, String> varMapOfIntegerString;

  public Map<String, ObjectId> varMapOfObjectId;

  public Map<String, Date> varMapOfDate;

  public Map<String, ClassB> varMapOfClassB;

  public Map<String, List<String>> varMapOfListOfString;

  public Map<String, List<Date>> varMapOfListOfDate;

  public Map<String, List<ClassB>> varMapOfListOfClassB;

  public Map<String, Map<String, String>> varMapOfMapOfString;

  public Set<String> varSetOfString;

  public Set<MyEnum> varSetOfEnum;

  @MongoRef
  public List<ClassA1> refListOfClassA;

  @MongoRef
  public List<ClassB> refListOfClassB;

  @MongoRef
  public List<ClassB> refListOfClassBEmpty;

  @MongoRef
  public List<ClassB> refListOfClassBEmptyWithInit = Lists.newArrayList();

  // nested collections
  public List<List<String>> varListOfListOfString;

  public List<List<ObjectId>> varListOfListOfObjectId;

  public List<List<ClassB>> varListOfListOfClassB;

  @MongoTransient
  public String varStringTransient;

  @MongoCustomConverter(codec = MyCodec.class)
  public Map<String, List<Map<String, String>>> varNestedMapWithCustomCodec = Maps.newHashMap();

  @MongoTransient
  public EmbeddedA1WithoutAnnotation varEmbeddedA1WithoutAnnotation;

  @MongoEntity
  public static class EmbeddedA1 {

    public ObjectId id;

    public String varString;

  }

  public static class EmbeddedA1WithoutAnnotation {

    @MongoTransient
    public ObjectId attachedObjectId;

    @MongoTransient
    public String varString;

  }

  @MongoEntity
  public static class SameNameEmbedded {

    public ObjectId id;
    public String vartString;

    @MongoRef
    public ClassB refClassB;

  }

}