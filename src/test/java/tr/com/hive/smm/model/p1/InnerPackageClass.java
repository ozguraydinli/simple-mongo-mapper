package tr.com.hive.smm.model.p1;

import org.bson.types.ObjectId;

import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.mapping.annotation.MongoId;
import tr.com.hive.smm.model.ClassA1Super;

@MongoEntity
public class InnerPackageClass extends ClassA1Super {


  @MongoId
  public ObjectId id;

  public String varString;

}