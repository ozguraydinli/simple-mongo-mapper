package tr.com.hive.smm.model;

import com.google.common.collect.Lists;

import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.mapping.annotation.MongoField;
import tr.com.hive.smm.mapping.annotation.MongoId;
import tr.com.hive.smm.mapping.annotation.MongoRef;

@MongoEntity
public class ClassA extends ClassASuper {

  @MongoId
  public ObjectId id;

  public ClassC varClassC;

  @MongoRef
  public ClassB refClassB;

  public ClassB varClassB;

  public ClassB varClassB2;

  public ClassB varClassB3;

  public ClassB2 varClazzB2;

  public String varString;

  public String varNullString = null;

  public MyEnum varEnum;

  public MyComplexEnum varComplexEnum;

  public int varInt;

  public Integer varBoxedInt;

  public Date varDate;

  public ObjectId varObjectId;

  public BigDecimal varBigDecimal;

  public String varBsonUndefined; // type is not actually, it could be something different than String

  // collections
  public List<String> varListOfString;

  public List<Integer> varListOfInteger1;

  public List<Integer> varListOfInteger2;

  public List<Date> varListOfDate;

  public List<MyEnum> varListOfEnum;

  public List<MyComplexEnum> varListOfComplexEnum;

  public List<ObjectId> varListOfObjectId;

  public List<ClassB> varListOfClassB;

  @MongoField(asEmptyArray = true)
  public List<ClassB> varListOfClassB_MongoField;

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
  public List<ClassB> refListOfClassB;

  @MongoRef
  public List<ClassB> refListOfClassBEmpty;

  @MongoRef
  public List<ClassB> refListOfClassBEmptyWithInit = Lists.newArrayList();

  // nested collections
  public List<List<String>> varListOfListOfString;

  public List<List<ObjectId>> varListOfListOfObjectId;

  public List<List<ClassB>> varListOfListOfClassB;

  public ClassA() {
  }

}