package tr.com.hive.smm.mapping;

import com.google.common.base.Strings;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import tr.com.hive.smm.MapperFactory;
import tr.com.hive.smm.mapping.annotation.MongoField;
import tr.com.hive.smm.mapping.annotation.MongoId;
import tr.com.hive.smm.mapping.annotation.MongoRef;
import tr.com.hive.smm.mapping.annotation.MongoTransient;

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

      String typeName = clazz.getTypeName();
      Class<?> forName = Class.forName(typeName);

      T t = (T) forName.newInstance();

      Class<?> tClass = t.getClass();
      Field[] declaredFields = tClass.getDeclaredFields();

      List<String> fields = Arrays.stream(declaredFields)
                                  .filter(field -> !Modifier.isStatic(field.getModifiers()))
                                  .filter(field -> !Modifier.isFinal(field.getModifiers()))
                                  .filter(f -> !f.isAnnotationPresent(MongoTransient.class))
                                  .map(Field::getName)
                                  .collect(Collectors.toList());

      for (String key : document.keySet()) {
        Object value = document.get(key);

        Field field = null;
        if ("_id".equals(key)) {
          Optional<Field> first = Arrays.stream(clazz.getDeclaredFields())
                                        .filter(f -> f.isAnnotationPresent(MongoId.class))
                                        .findFirst();
          if (first.isPresent()) {
            field = first.get();
          }
        }

        if (fields.contains(key)) {
          field = tClass.getDeclaredField(key);
        }

        if (field == null) {
          continue;
        }

        field.setAccessible(true);

        Class<?> aCLass = field.getType();
        Type genericType = field.getGenericType();

        MappedField mappedField = new MappedField(aCLass, genericType, depth);
        if (field.isAnnotationPresent(MongoRef.class)) {
          mappedField.setRef(true);
        } else if (field.isAnnotationPresent(MongoId.class)) {
          mappedField.setIsId(true);
        }

        Converter converter = mapperFactory.get(key, value, mappedField);
        field.set(t, converter.decode(value));
      }

      return t;

    } catch (Throwable t) {
      throw new MappingException("Mapping error", t);
    }
  }

  @Override
  public BsonValue encode(Object obj) {
    BsonDocument document = new BsonDocument();

    try {

      Class<?> aClass = obj.getClass();
      Field[] declaredFields = aClass.getDeclaredFields();

      List<Field> fields = Arrays.stream(declaredFields)
                                 .filter(field -> !Modifier.isStatic(field.getModifiers()))
                                 .filter(field -> !Modifier.isFinal(field.getModifiers()))
                                 .filter(f -> !f.isAnnotationPresent(MongoTransient.class))
                                 .collect(Collectors.toList());

      for (Field field : fields) {
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
      Field[] declaredFields = aClass.getDeclaredFields();

      List<Field> fields = Arrays.stream(declaredFields)
                                 .filter(field -> !Modifier.isStatic(field.getModifiers()))
                                 .filter(field -> !Modifier.isFinal(field.getModifiers()))
                                 .filter(f -> !f.isAnnotationPresent(MongoTransient.class))
                                 .collect(Collectors.toList());

      for (Field field : fields) {
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
