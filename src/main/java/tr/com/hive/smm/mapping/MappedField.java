package tr.com.hive.smm.mapping;

import java.lang.reflect.Type;

import tr.com.hive.smm.mapping.annotation.MongoField;

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

  private MongoField mongoField;

  private Class<? extends Converter> converterClazz;

  private boolean hasCustomConverter = false;

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

  public MongoField getMongoField() {
    return mongoField;
  }

  public void setMongoField(MongoField mongoField) {
    this.mongoField = mongoField;
  }

  public void setCustomConverter(Class<? extends Converter> converterClazz) {
    this.converterClazz = converterClazz;
  }

  public Class<? extends Converter> getConverterClazz() {
    return converterClazz;
  }

  public boolean hasCustomConverter() {
    return hasCustomConverter;
  }

  public void setHasCustomConverter(boolean hasCustomConverter) {
    this.hasCustomConverter = hasCustomConverter;
  }

}
