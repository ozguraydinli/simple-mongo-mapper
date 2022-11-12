package tr.com.hive.smm.model;

import org.bson.types.ObjectId;

import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.mapping.annotation.MongoId;

@MongoEntity
public class ClassWithMoreComplexEnum {

  @MongoId
  public ObjectId id;

  public MyMoreComplexEnum varMoreComplexEnum;

  public ClassWithMoreComplexEnum() {
  }

  public ClassWithMoreComplexEnum(ObjectId id) {
    this.id = id;
  }

}