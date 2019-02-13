package tr.com.hive.smm.model;

import org.bson.types.ObjectId;

import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.mapping.annotation.MongoId;

@MongoEntity
public class ClassC {

  @MongoId
  public ObjectId Id;

  public ClassC() {
  }

  public ClassC(ObjectId id) {
    this.Id = id;
  }

}