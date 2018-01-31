package tr.com.hive.smm;


import org.bson.BsonValue;
import org.bson.Document;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import tr.com.hive.smm.mapping.DocumentConverter;

/**
 * Created by ozgur on 3/24/16.
 */
@SuppressWarnings("unchecked")
public class SimpleMongoMapper {

  private static final Collection<Class<?>> knownEntities = new CopyOnWriteArrayList<>();

  public SimpleMongoMapper() {
  }

  public Collection<Class<?>> getKnownEntities() {
    return Collections.unmodifiableCollection(knownEntities);
  }

  public boolean isMongoEntity(Class<?> aClass) {
    return knownEntities.contains(aClass);
  }

  public void addEntity(Class<?> aClass) {
    if (!knownEntities.contains(aClass)) {
      knownEntities.add(aClass);
    }
  }

  public <T> T fromDocument(Document document, Class<T> clazz) {
    MapperFactory mapperFactory = new MapperFactory(this);

    return new DocumentConverter<>(mapperFactory, "", clazz, 0).decode(document);
  }

  public BsonValue toBsonValue(Object obj) {
    return ToBsonValue.toBsonValue(obj, this);
  }

  public Document toDocument(Object obj) {
    return ToDocument.toDocument(obj, this);
  }

}
