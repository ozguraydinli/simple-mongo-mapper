package tr.com.hive.smm;


import com.google.common.collect.Maps;

import com.mongodb.client.MongoDatabase;

import org.bson.BsonValue;
import org.bson.Document;

import java.util.Collection;
import java.util.Map;

import tr.com.hive.smm.mapping.DocumentConverter;

/**
 * Created by ozgur on 3/24/16.
 */
@SuppressWarnings("unchecked")
public class SimpleMongoMapper {

  private final Map<Class<?>, MappedClass> mappedClassCache = Maps.newConcurrentMap();

  private MongoDatabase mongoDatabase;

  private IndexHelper indexHelper;

  public SimpleMongoMapper() {
  }

  public SimpleMongoMapper(MongoDatabase mongoDatabase) {
    this.mongoDatabase = mongoDatabase;
    this.indexHelper = new IndexHelper(mongoDatabase);
  }

  public boolean isMongoEntity(Class<?> aClass) {
    return mappedClassCache.containsKey(aClass);
  }

  public void addEntity(Object obj) {
    // if it is not a collection type
    if (!(obj instanceof Collection) && !(obj instanceof Map)) {
      Class<?> aClass = obj.getClass();

      // and if it is not an annotation
      if (!aClass.isAnnotation() && !aClass.isEnum()) {
        addEntity(aClass);
      }
    }
  }

  public void addEntity(Class<?> aClass) {
    if (mappedClassCache.containsKey(aClass)) {
      return;
    }

    MappedClass mappedClass = new MappedClass(aClass);
    mappedClassCache.put(aClass, mappedClass);

    if (indexHelper != null) {
      indexHelper.createIndexes(mappedClass);
    }
  }

  public <T> T fromDocument(Document document, Class<T> clazz) {
    addEntity(clazz);
    return new DocumentConverter<>(new MapperFactory(this), "", clazz, 0).decode(document);
  }

  public BsonValue toBsonValue(Object obj) {
    addEntity(obj);
    return ToBsonValue.toBsonValue(obj, this);
  }

  public Document toDocument(Object obj) {
    addEntity(obj);
    return ToDocument.toDocument(obj, this);
  }

  public Map<Class<?>, MappedClass> getMappedClassCache() {
    return mappedClassCache;
  }

}
