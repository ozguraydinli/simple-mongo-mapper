package tr.com.hive.smm;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.Map;

import tr.com.hive.smm.mapping.Converter;
import tr.com.hive.smm.mapping.MappedField;

class ToBsonValue {

  public static BsonValue toBsonValue(Object obj, SimpleMongoMapper mapper) {
    if (obj instanceof Collection) {
      return toBsonValueAsBsonArray(obj, mapper);
    } else if (obj instanceof Map) {
      return toBsonValueAsBsonDocument(obj, mapper);
    } else {
      return toBsonValueAsBsonValue(obj, mapper);
    }
  }

  private static BsonValue toBsonValueAsBsonValue(Object obj, SimpleMongoMapper mapper) {
    MapperFactory mapperFactory = new MapperFactory(mapper);

    MappedField mappedField = new MappedField(obj.getClass(), obj.getClass().getGenericSuperclass());
    Converter converter = mapperFactory.get("", obj, mappedField);

    return converter.encode(obj);
  }

  private static BsonValue toBsonValueAsBsonDocument(Object obj, SimpleMongoMapper mapper) {
    BsonDocument bsonDocument = new BsonDocument();
    Map map = (Map) obj;

    for (Object o : map.keySet()) {
      Object value = map.get(o);

      if (Enum.class.isAssignableFrom(o.getClass())) {
        bsonDocument.put(((Enum) o).name(), toBsonValue(value, mapper));
      } else if (ObjectId.class.isAssignableFrom(o.getClass())) {
        bsonDocument.put(o.toString(), toBsonValue(value, mapper));
      } else if (o.getClass() == Integer.class && Integer.class.isAssignableFrom(o.getClass())) {
        bsonDocument.put(String.valueOf(o), toBsonValue(value, mapper));
      } else {
        bsonDocument.put((String) o, toBsonValue(value, mapper));
      }
    }

    return bsonDocument;
  }

  private static BsonValue toBsonValueAsBsonArray(Object obj, SimpleMongoMapper mapper) {
    BsonArray bsonArray = new BsonArray();
    Collection collection = (Collection) obj;

    for (Object o : collection) {
      bsonArray.add(toBsonValue(o, mapper));
    }

    return bsonArray;
  }

}
