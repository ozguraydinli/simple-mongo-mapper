package tr.com.hive.smm.model;

import org.bson.types.ObjectId;

import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.mapping.annotation.MongoId;

@MongoEntity
public class ClassA2 extends ClassA1Super {

  @MongoId
  public ObjectId id;

  @MongoEntity
  public static class SameNameEmbedded {

    public ObjectId id;
    public String vartString;

  }

}