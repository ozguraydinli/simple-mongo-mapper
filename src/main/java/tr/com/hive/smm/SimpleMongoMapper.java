package tr.com.hive.smm;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import org.bson.BsonValue;
import org.bson.Document;

import java.util.List;
import java.util.Map;

import tr.com.hive.smm.mapping.Converter;
import tr.com.hive.smm.mapping.DocumentConverter;
import tr.com.hive.smm.mapping.annotation.MongoEntity;

/**
 * Created by ozgur on 3/24/16.
 */
@SuppressWarnings("unchecked")
public class SimpleMongoMapper {

  private final Map<Class<?>, MappedClass> mappedClassCache = Maps.newConcurrentMap();
  private final Map<Class<?>, Converter> registeredTypes = Maps.newConcurrentMap();

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

  public void registerType(Class<?> clazz, Converter converter) {
    registeredTypes.put(clazz, converter);
  }

  public boolean isRegisteredType(Class<?> clazz) {
    return registeredTypes.containsKey(clazz);
  }

  public Converter getConverter(Class<?> aClass) {
    return registeredTypes.get(aClass);
  }

  public void addEntity(Object obj) {
    addEntity(obj.getClass());
  }

  public void addEntity(Class<?> aClass) {
    if (!aClass.isAnnotationPresent(MongoEntity.class)) {
      return;
    }

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

  public <T> List<T> fromMongoIterable(MongoIterable<Document> iterable, Class<T> clazz) {
    if (iterable == null) {
      throw new NullPointerException("iterable cannot be null");
    }

    addEntity(clazz);

    List<T> list = Lists.newArrayList();

    for (Document document : iterable) {
      list.add(new DocumentConverter<>(new MapperFactory(this), "", clazz, 0).decode(document));
    }

    return list;
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
