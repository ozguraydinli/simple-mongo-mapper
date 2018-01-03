package tr.com.hive.smm.mapping;

import java.lang.reflect.Type;

/**
 * Created by ozgur on 4/4/17.
 */
public class MappedField {

  private Class<?> aCLass;
  private Type genericType;
  private int depth;
  private boolean ref;
  private boolean isId;
  private String mongoIdAnnotationValue;

  public MappedField(Class<?> aCLass, Type genericType) {
    this.aCLass = aCLass;
    this.genericType = genericType;
  }

  public MappedField(Class<?> aCLass, Type genericType, int depth) {
    this.aCLass = aCLass;
    this.genericType = genericType;
    this.depth = depth;
  }

  public void setRef(boolean ref) {
    this.ref = ref;
  }

  public Class<?> getCLass() {
    return aCLass;
  }

  public Type getGenericType() {
    return genericType;
  }

  public boolean isRef() {
    return ref;
  }

  public void setIsId(boolean isId) {
    this.isId = isId;
  }

  public boolean isId() {
    return isId;
  }

  public int getDepth() {
    return depth;
  }

  public void setMongoIdValue(String mongoIdAnnotationValue) {
    this.mongoIdAnnotationValue = mongoIdAnnotationValue;
  }

  public String getMongoIdAnnotationValue() {
    return mongoIdAnnotationValue;
  }

}
