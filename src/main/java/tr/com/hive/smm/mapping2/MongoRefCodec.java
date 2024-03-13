package tr.com.hive.smm.mapping2;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tr.com.hive.smm.util.ReflectionUtils;

@SuppressWarnings("unchecked")
public class MongoRefCodec<T> implements Codec<T> {

  private final Map<String, MappedClass> classMap;
  private final Class<?> clazz;
  private final Field field;
  private final Object dummyInstance;

  public MongoRefCodec(Map<String, MappedClass> classMap, Field field, Object dummyInstance) {
    this.classMap = classMap;
    this.clazz = field.getType();
    this.field = field;
    this.dummyInstance = dummyInstance;
  }

  @Override
  public T decode(BsonReader reader, DecoderContext decoderContext) {
    if (Collection.class.isAssignableFrom(clazz)) {
      Object fieldInit = ReflectionUtils.getFieldValue(field, dummyInstance);

      Collection<Object> collection;
      if (fieldInit == null) {
        collection = createCollection();
      } else {
        collection = switch (fieldInit) {
          case ArrayList<?> __ -> new ArrayList<>();
          case LinkedHashSet<?> __ -> new LinkedHashSet<>();
          case HashSet<?> __ -> new HashSet<>();
          case LinkedList<?> __ -> new LinkedList<>();
          default -> throw new IllegalStateException("Unsupported type: " + fieldInit.getClass());
        };
      }

      reader.readStartArray();

      while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
        T t = decodeOne(reader);
        collection.add(t);
      }

      reader.readEndArray();

      return (T) collection;
    }

    return decodeOne(reader);
  }


  @SuppressWarnings("rawtypes")
  @Override
  public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
    Class<?> valueClass = value.getClass();

    if (Collection.class.isAssignableFrom(valueClass)) {
      writer.writeStartArray();

      Collection list = (Collection) value;
      for (Object o : list) {
        Class<?> objectClass = o.getClass();

        encodeOne(writer, objectClass.getSimpleName(), o);
      }

      writer.writeEndArray();

      return;
    }

    encodeOne(writer, valueClass.getSimpleName(), value);
  }

  private void encodeOne(BsonWriter writer, String objectClass, Object o) {
    writer.writeStartDocument();

    writer.writeString("$ref", objectClass);

    MappedClass mappedClass = classMap.get(objectClass);
    Field idField = mappedClass.getIdField();
    ObjectId fieldValue = ReflectionUtils.getFieldValue(idField, o);
    writer.writeObjectId("$id", fieldValue);

    writer.writeEndDocument();
  }

  @Override
  public Class<T> getEncoderClass() {
    return (Class<T>) clazz;
  }

  private Collection<Object> createCollection() {
    try {
      Collection<Object> collection;

      if (clazz.isInterface()) {
        if (clazz == List.class) {
          collection = ArrayList.class.getDeclaredConstructor().newInstance();
        } else if (clazz == Set.class) {
          collection = HashSet.class.getDeclaredConstructor().newInstance();
        } else {
          throw new IllegalStateException("Unknown collection type : " + clazz.getName());
        }
      } else {
        collection = (Collection<Object>) clazz.getDeclaredConstructor().newInstance();
      }

      return collection;
    } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  private T decodeOne(BsonReader reader) {
    reader.readStartDocument();

    String ref = reader.readString("$ref");
    ObjectId id = reader.readObjectId("$id");

    try {
      MappedClass mappedClass = classMap.get(ref);

      Class<?> clazz = mappedClass.getMappedClass();

      T instance = (T) clazz.getDeclaredConstructor().newInstance();

      Field idField = mappedClass.getIdField();
      ReflectionUtils.setFieldValue(idField, instance, id);

      reader.readEndDocument();

      return instance;

    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

}
