package tr.com.hive.smm;

import com.mongodb.DBRef;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import tr.com.hive.smm.mapping.*;
import tr.com.hive.smm.mapping.annotation.MongoEntity;

/**
 * Created by ozgur on 4/7/17.
 */
public class MapperFactory {

  private final static Map<Class<?>, Class<?>> primitiveToClassMap = new HashMap<>();

  static {
    primitiveToClassMap.put(boolean.class, Boolean.class);
    primitiveToClassMap.put(byte.class, Byte.class);
    primitiveToClassMap.put(short.class, Short.class);
    primitiveToClassMap.put(char.class, Character.class);
    primitiveToClassMap.put(int.class, Integer.class);
    primitiveToClassMap.put(long.class, Long.class);
    primitiveToClassMap.put(float.class, Float.class);
    primitiveToClassMap.put(double.class, Double.class);
    primitiveToClassMap.put(Date.class, Date.class);
  }

  private SimpleMongoMapper mongoMapper;

  public MapperFactory(SimpleMongoMapper mongoMapper) {
    this.mongoMapper = mongoMapper;
  }

  public Converter get(String key, Object value, MappedField mappedField) {
    Class<?> aClass = mappedField.getCLass();
    Type genericType = mappedField.getGenericType();

    try {

      if (value == null) {
        return new EmptyConverter();
      }

      if (value instanceof String || value instanceof Enum) {
        if (aClass.isEnum()) {
          return new EnumConverter(this, key, aClass);
        } else if (aClass == String.class) {
          return new StringConverter(this, key, aClass);
        } else {
          throw new MappingException("Something went wrong: " + key);
        }
      } else if (isKnownEntity(value)) {
        Class<?> clazz1 = Class.forName(genericType.getTypeName());
        if (mappedField.isRef()) {
          return new MongoRefConverter(this, key, clazz1);
        } else {
          int depth = mappedField.getDepth();
          return new DocumentConverter<>(this, key, clazz1, ++depth);
        }
      } else if (value instanceof Document) {
        if (Map.class.isAssignableFrom(aClass)) {
          return new MapConverter(this, key, aClass, (ParameterizedType) genericType);
        } else if (Collection.class.isAssignableFrom(aClass) || isKnownType(aClass)) {
          throw new MappingException("Cannot parse: key" + key);
        } else {
          Class<?> clazz1 = Class.forName(genericType.getTypeName());
          int depth = mappedField.getDepth();
          return new DocumentConverter<>(this, key, clazz1, ++depth);
        }
      } else if (Map.class.isAssignableFrom(aClass)) {
        return new MapConverter(this, key, aClass, (ParameterizedType) genericType);
      } else if (value instanceof ObjectId) {
        return new ObjectIdConverter(this, key, aClass);
      } else if (value instanceof DBRef) {
        if (mappedField.isRef()) {
          return new MongoRefConverter(this, key, aClass);
        } else {
          throw new MappingException("Returned document has a DBRef for this field: " + key);
        }
      } else if (Collection.class.isAssignableFrom(value.getClass())) {
        if (Collection.class.isAssignableFrom(aClass)) {
          return new CollectionConverter(this, key, aClass, (ParameterizedType) genericType);
        } else {
          throw new MappingException("Cannot parse: key" + key);
        }
      } else {
        if (aClass.isPrimitive()) {
          if (primitiveToClassMap.get(aClass) == value.getClass()) {
            return new PrimitiveConverter(this, key, aClass);
          } else {
            throw new MappingException("Unknown primitive type : " + value.getClass().getName());
          }
        } else if (isKnownType(aClass)) {
          if (aClass == value.getClass()) {
            return new PrimitiveConverter(this, key, aClass);
          } else {
            throw new MappingException("Unknown primitive type : " + value.getClass().getName());
          }
        } else {
          Class<?> clazz1 = Class.forName(genericType.getTypeName());
          int depth = mappedField.getDepth();
          return new DocumentConverter<>(this, key, clazz1, ++depth);
        }
      }
    } catch (Throwable t) {
      throw new MappingException("Mapping error", t);
    }
  }

  public SimpleMongoMapper getMongoMapper() {
    return mongoMapper;
  }

  private boolean isKnownEntity(Object value) {
    if (!mongoMapper.isMongoEntity(value.getClass())) {

      if (value.getClass().isAnnotationPresent(MongoEntity.class)) {
        mongoMapper.addEntity(value.getClass());
        return true;
      }

      return false;
    }

    return true;
  }

  public static boolean isKnownType(Class<?> clazz) {
    return Integer.class.isAssignableFrom(clazz) ||
           Double.class.isAssignableFrom(clazz) ||
           Boolean.class.isAssignableFrom(clazz) ||
           Byte.class.isAssignableFrom(clazz) ||
           Short.class.isAssignableFrom(clazz) ||
           Character.class.isAssignableFrom(clazz) ||
           Long.class.isAssignableFrom(clazz) ||
           Float.class.isAssignableFrom(clazz) ||
           String.class.isAssignableFrom(clazz) ||
           Date.class.isAssignableFrom(clazz);
  }

}
