package tr.com.hive.smm.mapping;

import com.google.common.base.Strings;

import com.mongodb.DBRef;

import org.bson.*;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

import tr.com.hive.smm.MappedClass;
import tr.com.hive.smm.MapperFactory;
import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.mapping.annotation.MongoId;

/**
 * Created by ozgur on 4/3/17.
 */
public class MongoRefConverter extends AbstractConverter implements Converter {

  public MongoRefConverter(MapperFactory mapperFactory, String key, Class<?> clazz) {
    super(mapperFactory, key, clazz);
  }

  @Override
  public Object decode(Object obj) {
    try {
      if (obj instanceof DBRef) {
        String collectionName = ((DBRef) obj).getCollectionName();

        if (!clazz.isAnnotationPresent(MongoEntity.class)) {
          throw new IllegalStateException("Cannot map unkown entity. You need to provide MongoEntity annotation. " + clazz.getName());
        }

        mapperFactory.getMongoMapper().getMappedClassCache().computeIfAbsent(clazz, c -> new MappedClass(clazz));
        MappedClass mappedClass = mapperFactory.getMongoMapper().getMappedClassCache().get(clazz);

        String value = mappedClass.getCollectionName();
        boolean valid = value.equals(collectionName);

        // silently ignore if it is not valid
        if (valid) {
          Object instance = clazz.newInstance();

          Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(MongoId.class))
                .findFirst()
                .ifPresent(field -> {
                  try {
                    field.setAccessible(true);
                    field.set(instance, ((DBRef) obj).getId());
                  } catch (IllegalAccessException e) {
                    e.printStackTrace();
                  }
                });

          return instance;
        }
      }
    } catch (Throwable t) {
      throw new MappingException("Mapping error", t);
    }

    return null;
  }

  @Override
  public BsonValue encode(Object obj) throws MappingException {
    DBRef dbRef = dbRef(obj);

    BsonDocument bsonDocument = new BsonDocument();

    bsonDocument.put("$ref", new BsonString(dbRef.getCollectionName()));
    bsonDocument.put("$id", new BsonObjectId((ObjectId) dbRef.getId()));

    return bsonDocument;
  }

  @Override
  public Object encodeToDocument(Object obj) throws MappingException {
    DBRef dbRef = dbRef(obj);

    Document document = new Document();

    document.put("$ref", dbRef.getCollectionName());
    document.put("$id", dbRef.getId());

    return document;
  }

  public DBRef dbRef(Object obj) throws MappingException {
    try {
      Class<?> clzz = obj.getClass();

      Optional<Field> first = Arrays.stream(clzz.getDeclaredFields())
                                    .filter(field -> field.isAnnotationPresent(MongoId.class))
                                    .findFirst();

      if (first.isPresent()) {
        Field field = first.get();
        field.setAccessible(true);

        Object o = field.get(obj);

        if (!(o instanceof ObjectId)) {
          throw new MappingException("id field must be ObjectId type:  " + clzz.getName());
        }

        if (clzz.isAnnotationPresent(MongoEntity.class)) {
          MongoEntity annotation = clzz.getAnnotation(MongoEntity.class);
          String value = annotation.value();
          if (!Strings.isNullOrEmpty(value)) {
            return new DBRef(value, o);
          }
        }

        return new DBRef(clzz.getSimpleName(), o);

      } else {
        throw new MappingException("Missing @MongoId annotation: " + clzz.getName());
      }

    } catch (Throwable t) {
      throw new MappingException("Mapping error", t);
    }
  }


}
