package tr.com.hive.smm.smodel2;

import org.bson.types.ObjectId;

import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.mapping.annotation.MongoId;
import tr.com.hive.smm.model.ClassA1Super;

@MongoEntity
public class ClassShouldNotBeMapped extends ClassA1Super {


  @MongoId
  public ObjectId id;

  public String varString;

}