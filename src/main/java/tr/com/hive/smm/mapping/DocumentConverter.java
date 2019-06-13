package tr.com.hive.smm.mapping;

import com.google.common.base.Strings;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import tr.com.hive.smm.MapperFactory;
import tr.com.hive.smm.mapping.annotation.*;
import tr.com.hive.smm.util.ReflectionUtils;

/**
 * Created by ozgur on 4/3/17.
 */
@SuppressWarnings("unchecked")
public class DocumentConverter<T> extends AbstractConverter implements Converter {

  private int depth;

  public DocumentConverter(MapperFactory mapperFactory, String key, Class<T> clazz, int depth) {
    super(mapperFactory, key, clazz);
    this.depth = depth;
  }

  public DocumentConverter(MapperFactory mapperFactory, String key, int depth) {
    super(mapperFactory, key, null);
    this.depth = depth;
  }

  @Override
  public T decode(Object obj) {
    try {
      Document document = (Document) obj;

      Constructor<?> constructor = MapperUtil.getDefaultConstructor(clazz);

      T t = (T) constructor.newInstance();

      Class<?> tClass = t.getClass();

      Map<String, Field> fieldMap = getFieldMap(tClass);

      for (String key : document.keySet()) {
        Object value = document.get(key);

        Field field = null;
        if ("_id".equals(key)) {
          Optional<Field> first = fieldMap.values()
                                          .stream()
                                          .filter(f -> f.isAnnotationPresent(MongoId.class))
                                          .findFirst();
          if (first.isPresent()) {
            field = first.get();
          } else if (fieldMap.containsKey(key)) {
            field = fieldMap.get(key);
          }

        } else if (fieldMap.containsKey(key)) {
          field = fieldMap.get(key);
        }

        if (field != null) {

          field.setAccessible(true);

          Class<?> aCLass = field.getType();
          Type genericType = field.getGenericType();

          MappedField mappedField = new MappedField(aCLass, genericType, depth);
          if (field.isAnnotationPresent(MongoRef.class)) {
            mappedField.setRef(true);
          } else if (field.isAnnotationPresent(MongoId.class)) {
            mappedField.setIsId(true);
          } else if (field.isAnnotationPresent(MongoCustomConverter.class)) {
            mappedField.setHasCustomConverter(true);

            MongoCustomConverter annotation = field.getAnnotation(MongoCustomConverter.class);
            Class<? extends Converter> clazz = annotation.value();
            if (clazz == EmptyConverter.class) {
              Class<?> converterClass = annotation.converterClass();
              if (converterClass == EmptyConverter.class) {
                throw new MappingException("value or converterClass must be set for MongoCustomConverter. " + key);
              }

              mappedField.setCustomConverter(converterClass);
            } else {
              mappedField.setCustomConverter(clazz);
            }
          }

          Converter converter = mapperFactory.get(key, value, mappedField);
          field.set(t, converter.decode(value));
        }
      }

      return t;

    } catch (Throwable t) {
      throw new MappingException("Mapping error", t);
    }
  }

  private Map<String, Field> getFieldMap(Class<?> tClass) {
    return ReflectionUtils.getAllFields(tClass)
                          .stream()
                          .filter(field -> !field.isSynthetic())
                          .filter(field -> !Modifier.isStatic(field.getModifiers()))
                          .filter(field -> !Modifier.isFinal(field.getModifiers()))
                          .filter(f -> !f.isAnnotationPresent(MongoTransient.class))
                          .collect(Collectors.toMap(Field::getName, f -> f, throwingMerger(), LinkedHashMap::new));
  }

  // merge function taken directly from JDK - Collectors.java
  private static <T> BinaryOperator<T> throwingMerger() {
    return (u, v) -> {
      throw new IllegalStateException(String.format("Duplicate key %s", u));
    };
  }


  @Override
  public BsonValue encode(Object obj) {
    BsonDocument document = new BsonDocument();

    try {

      Class<?> aClass = obj.getClass();
      Map<String, Field> fieldMap = getFieldMap(aClass);

      for (Field field : fieldMap.values()) {
        field.setAccessible(true);

        Class<?> aCLass = field.getType();
        Type genericType = field.getGenericType();

        MappedField mappedField = new MappedField(aCLass, genericType);
        if (field.isAnnotationPresent(MongoRef.class)) {
          mappedField.setRef(true);
        } else if (field.isAnnotationPresent(MongoId.class)) {
          mappedField.setIsId(true);

          MongoId mongoIdAnnotation = field.getAnnotation(MongoId.class);
          String mongoIdAnnotationValue = mongoIdAnnotation.value();

          mappedField.setMongoIdValue(mongoIdAnnotationValue);
        } else if (field.isAnnotationPresent(MongoCustomConverter.class)) {
          mappedField.setHasCustomConverter(true);

          MongoCustomConverter annotation = field.getAnnotation(MongoCustomConverter.class);
          Class<? extends Converter> clazz = annotation.value();
          if (clazz == EmptyConverter.class) {
            Class<?> converterClass = annotation.converterClass();
            if (converterClass == EmptyConverter.class) {
              throw new MappingException("value or converterClass must be set for MongoCustomConverter. " + key);
            }

            mappedField.setCustomConverter(converterClass);
          } else {
            mappedField.setCustomConverter(clazz);
          }
        }

        Object value = field.get(obj);

        if (value != null) {
          Converter converter = mapperFactory.get(key, value, mappedField);
          BsonValue encode = converter.encode(value);

          if (encode != null) {
            if (mappedField.isId()) {
              if (depth == 0) {
                document.put("_id", encode);
              } else {
                String mongoIdAnnotationValue = mappedField.getMongoIdAnnotationValue();
                if (Strings.isNullOrEmpty(mongoIdAnnotationValue)) {
                  document.put(field.getName(), encode);
                } else {
                  document.put("_id", encode);
                }
              }
            } else {
              document.put(field.getName(), encode);
            }
          }
        }
      }

    } catch (IllegalAccessException t) {
      throw new MappingException("Mapping error", t);
    }

    return document;
  }

  @Override
  public Document encodeToDocument(Object obj) throws MappingException {
    Document document = new Document();

    try {

      Class<?> aClass = obj.getClass();
      Map<String, Field> fieldMap = getFieldMap(aClass);

      for (Field field : fieldMap.values()) {
        field.setAccessible(true);

        Class<?> aCLass = field.getType();
        Type genericType = field.getGenericType();

        MappedField mappedField = new MappedField(aCLass, genericType);
        if (field.isAnnotationPresent(MongoRef.class)) {
          mappedField.setRef(true);
        } else if (field.isAnnotationPresent(MongoId.class)) {
          mappedField.setIsId(true);
        }

        if (field.isAnnotationPresent(MongoField.class)) {
          mappedField.setMongoField(field.getAnnotation(MongoField.class));
        }

        Object value = field.get(obj);

        if (value != null) {
          Converter converter = mapperFactory.get(key, value, mappedField);
          Object encodeToDocument = converter.encodeToDocument(value);

          if (encodeToDocument != null) {
            if (mappedField.isId()) {
              if (depth == 0) {
                document.put("_id", encodeToDocument);
              } else {
                String mongoIdAnnotationValue = mappedField.getMongoIdAnnotationValue();
                if (Strings.isNullOrEmpty(mongoIdAnnotationValue)) {
                  document.put(field.getName(), encodeToDocument);
                } else {
                  document.put("_id", encodeToDocument);
                }
              }
            } else {
              document.put(field.getName(), encodeToDocument);
            }
          }
        }
      }

    } catch (IllegalAccessException t) {
      throw new MappingException("Mapping error", t);
    }

    return document;
  }

}
