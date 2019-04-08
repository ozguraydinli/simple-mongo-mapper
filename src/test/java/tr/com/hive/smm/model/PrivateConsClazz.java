package tr.com.hive.smm.model;

import com.google.common.base.MoreObjects;

import org.bson.types.ObjectId;

import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.mapping.annotation.MongoId;

@MongoEntity
public class PrivateConsClazz {

  @MongoId
  private ObjectId id;

  private String stringField;

  private PrivateConsClazz() {
  }

  public PrivateConsClazz(String stringField) {
    this.stringField = stringField;
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getStringField() {
    return stringField;
  }

  public void setStringField(String stringField) {
    this.stringField = stringField;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
                      .add("id", id)
                      .add("stringField", stringField)
                      .toString();
  }

}
