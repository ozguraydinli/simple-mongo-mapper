package tr.com.hive.smm.mapping2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappedClass {

  private final Class<?> clazz;
  private Field idField;

  private final List<Field> mongoRefFields = new ArrayList<>();

  private final List<Field> mongoTransientFields = new ArrayList<>();

  private final Map<Field, Class<?>> fieldToCodecClassMap = new HashMap<>();

  private final Map<Field, String> fieldToMongoFieldMap = new HashMap<>();

  public MappedClass(Class<?> clazz) {
    this.clazz = clazz;
  }

  public void setIdField(Field idField) {
    this.idField = idField;
  }

  public void addMongoRefField(Field field) {
    mongoRefFields.add(field);
  }

  public void addMongoTransientField(Field field) {
    mongoTransientFields.add(field);
  }

  public void addCustomConverter(Field field, Class<?> codecClass) {
    fieldToCodecClassMap.put(field, codecClass);
  }

  public void addMongoField(Field field, String value) {
    fieldToMongoFieldMap.put(field, value);
  }

  public Field getIdField() {
    return idField;
  }

  public List<Field> getMongoRefFields() {
    return Collections.unmodifiableList(mongoRefFields);
  }

  public List<Field> getMongoTransientFields() {
    return Collections.unmodifiableList(mongoTransientFields);
  }

  public Map<Field, String> getFieldToMongoFieldMap() {
    return Collections.unmodifiableMap(fieldToMongoFieldMap);
  }

  public Map<Field, Class<?>> getFieldToCodecClassMap() {
    return Collections.unmodifiableMap(fieldToCodecClassMap);
  }

  public Class<?> getMappedClass() {
    return clazz;
  }

  public boolean shouldBuildClassModel() {
    return !mongoRefFields.isEmpty() ||
           !mongoTransientFields.isEmpty() ||
           !fieldToCodecClassMap.isEmpty() ||
           !fieldToMongoFieldMap.isEmpty();
  }

}
