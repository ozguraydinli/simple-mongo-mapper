package tr.com.hive.smm.model;

import org.bson.types.ObjectId;

import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.mapping.annotation.MongoId;
import tr.com.hive.smm.mapping.annotation.index.Field;
import tr.com.hive.smm.mapping.annotation.index.Index;
import tr.com.hive.smm.mapping.annotation.index.IndexOptions;
import tr.com.hive.smm.mapping.annotation.index.IndexType;
import tr.com.hive.smm.mapping.annotation.index.Indexes;

@Indexes({
  @Index(
    fields = {@Field(value = "field1")},
    options = @IndexOptions(name = "field1Index")),
  @Index(
    fields = {@Field(value = "field2", type = IndexType.DESC)},
    options = @IndexOptions(name = "field2Index")),
  @Index(
    fields = {@Field(value = "uniqueId")},
    options = @IndexOptions(name = "uniqueIdIndex", unique = true)),
  @Index(
    fields = {@Field(value = "fieldCmp1"), @Field(value = "fieldCmp2"), @Field(value = "fieldCmp3")},
    options = @IndexOptions(name = "cmpIndex"))
})
@MongoEntity
public class IndexClass {

  @MongoId
  public ObjectId id;

  private int uniqueId;

  private String field1;

  private String field2;

  private String fieldCmp1;

  private String fieldCmp2;

  private String fieldCmp3;

  public IndexClass() {
  }

}
