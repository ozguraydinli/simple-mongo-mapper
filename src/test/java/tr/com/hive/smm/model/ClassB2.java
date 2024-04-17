package tr.com.hive.smm.model;

import org.bson.types.ObjectId;

import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.mapping.annotation.MongoId;

@MongoEntity
public class ClassB2 {

//  @MongoId
  public ObjectId _id;

  public double varDouble;

  public ClassB2() {
  }

  public static ClassB2 create(ObjectId id) {
    ClassB2 classB2 = new ClassB2();
    classB2._id = id;
    return classB2;
  }

}