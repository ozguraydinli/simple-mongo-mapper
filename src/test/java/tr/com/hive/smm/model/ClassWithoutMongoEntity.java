package tr.com.hive.smm.model;

import org.bson.types.ObjectId;

import tr.com.hive.smm.mapping.annotation.MongoId;

public class ClassWithoutMongoEntity {

  @MongoId
  public ObjectId id;

  public String varString;

}
