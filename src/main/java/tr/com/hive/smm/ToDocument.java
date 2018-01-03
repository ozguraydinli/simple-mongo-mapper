package tr.com.hive.smm;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.Map;

import tr.com.hive.smm.mapping.DocumentConverter;

class ToDocument {

  public static Document toDocument(Object obj, SimpleMongoMapper mapper) {
    if (obj instanceof Collection) {
      throw new UnsupportedOperationException("Cannot convert Collection object to a Document. You are probably doing something wrong! " +
                                              "Object should be either Map or Java Class.");
    } else if (obj instanceof Map) {
      return toDocument_Map(obj, mapper);
    } else {
      return toDocumentInner(obj, mapper);
    }
  }

  private static Document toDocumentInner(Object obj, SimpleMongoMapper mapper) {
    MapperFactory mapperFactory = new MapperFactory(mapper);

    return new DocumentConverter<>(mapperFactory, "", 0).encodeToDocument(obj);
  }

  private static Document toDocument_Map(Object obj, SimpleMongoMapper mapper) {
    Document document = new Document();
    Map map = (Map) obj;

    for (Object o : map.keySet()) {
      Object value = map.get(o);

      if (Enum.class.isAssignableFrom(o.getClass())) {
        document.put(((Enum) o).name(), toDocument(value, mapper));
      } else if (ObjectId.class.isAssignableFrom(o.getClass())) {
        document.put(o.toString(), toDocument(value, mapper));
      } else if (o.getClass() == Integer.class && Integer.class.isAssignableFrom(o.getClass())) {
        document.put(String.valueOf(o), toDocument(value, mapper));
      } else {
        document.put((String) o, toDocument(value, mapper));
      }
    }

    return document;
  }

}
