package tr.com.hive.smm.mapping;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import tr.com.hive.smm.MapperFactory;

/**
 * Created by ozgur on 4/3/17.
 */
@SuppressWarnings("unchecked")
public class MapConverter extends AbstractConverter implements Converter {

  private ParameterizedType genericType;

  public MapConverter(MapperFactory mapperFactory, String key, Class<?> clazz, ParameterizedType genericType) {
    super(mapperFactory, key, clazz);
    this.genericType = genericType;
  }

  @Override
  public Object decode(Object obj) {
    try {
      Map<Object, Object> map;
      if (clazz.isInterface()) {
        map = MapperUtil.newInstance(clazz, HashMap.class);
      } else {
        map = (Map<Object, Object>) clazz.newInstance();
      }

      Type[] actualTypeArguments = genericType.getActualTypeArguments();

      Type typeArgument1 = actualTypeArguments[0];
      Class<?> cls1 = Class.forName(typeArgument1.getTypeName());
      if (cls1 != String.class &&
          cls1 != Integer.class &&
          !Enum.class.isAssignableFrom(cls1) &&
          !ObjectId.class.isAssignableFrom(cls1)) {
        throw new MappingException("key must be String, Integer, Enum or ObjectId type: " + cls1.getName());
      }

      Class<?> cls2;
      Type typeArgument2 = actualTypeArguments[1];
      if (typeArgument2 instanceof ParameterizedType) {
        cls2 = Class.forName(((ParameterizedType) typeArgument2).getRawType().getTypeName());
      } else {
        cls2 = Class.forName(typeArgument2.getTypeName());
      }

      Document document = (Document) obj;
      for (String mapKey : document.keySet()) {
        Object o = document.get(mapKey);

        MappedField mappedField = new MappedField(cls2, typeArgument2);

        Converter mapper = mapperFactory.get(mapKey, o, mappedField);

        if (Enum.class.isAssignableFrom(cls1)) {
          map.put(new EnumConverter(mapperFactory, "", cls1).decode(mapKey), mapper.decode(o));
        } else if (ObjectId.class.isAssignableFrom(cls1)) {
          map.put(new ObjectIdConverter(mapperFactory, "", cls1).decode(new ObjectId(mapKey)), mapper.decode(o));
        } else if (cls1 == Integer.class && Integer.class.isAssignableFrom(cls1)) {
          map.put(Integer.valueOf(mapKey), mapper.decode(o));
        } else {
          map.put(mapKey, mapper.decode(o));
        }
      }

      return map;

    } catch (Throwable t) {
      throw new MappingException("Mapping error", t);
    }
  }

  @Override
  public BsonValue encode(Object obj) throws MappingException {
    try {
      if (obj instanceof Map) {
        Class<?> cls2;
        Type[] actualTypeArguments = genericType.getActualTypeArguments();
        Type typeArgument2 = actualTypeArguments[1];
        if (typeArgument2 instanceof ParameterizedType) {
          cls2 = Class.forName(((ParameterizedType) typeArgument2).getRawType().getTypeName());
        } else {
          cls2 = Class.forName(typeArgument2.getTypeName());
        }

        Map map = (Map) obj;

        if (map.size() == 0) {
          return null;
        }

        BsonDocument bsonDocument = new BsonDocument();
        for (Object o : map.keySet()) {
          Object value = map.get(o);

          MappedField mappedField = new MappedField(cls2, typeArgument2);

          Converter converter = mapperFactory.get(key, value, mappedField);

          if (Enum.class.isAssignableFrom(o.getClass())) {
            bsonDocument.put(((Enum) o).name(), converter.encode(value));
          } else if (ObjectId.class.isAssignableFrom(o.getClass())) {
            bsonDocument.put(o.toString(), converter.encode(value));
          } else if (o.getClass() == Integer.class && Integer.class.isAssignableFrom(o.getClass())) {
            bsonDocument.put(String.valueOf(o), converter.encode(value));
          } else {
            bsonDocument.put((String) o, converter.encode(value));
          }
        }

        return bsonDocument;
      }

      throw new MappingException("Expecting a Map: " + obj.getClass().getName());

    } catch (Throwable t) {
      throw new MappingException("Mapping error", t);
    }
  }

  @Override
  public Object encodeToDocument(Object obj) throws MappingException {
    try {
      if (obj instanceof Map) {
        Class<?> cls2;
        Type[] actualTypeArguments = genericType.getActualTypeArguments();
        Type typeArgument2 = actualTypeArguments[1];
        if (typeArgument2 instanceof ParameterizedType) {
          cls2 = Class.forName(((ParameterizedType) typeArgument2).getRawType().getTypeName());
        } else {
          cls2 = Class.forName(typeArgument2.getTypeName());
        }

        Map map = (Map) obj;

        if (map.size() == 0) {
          return null;
        }

        Document document = new Document();

        for (Object o : map.keySet()) {
          Object value = map.get(o);

          MappedField mappedField = new MappedField(cls2, typeArgument2);

          Converter converter = mapperFactory.get(key, value, mappedField);

          if (Enum.class.isAssignableFrom(o.getClass())) {
            document.put(((Enum) o).name(), converter.encodeToDocument(value));
          } else if (ObjectId.class.isAssignableFrom(o.getClass())) {
            document.put(o.toString(), converter.encodeToDocument(value));
          } else if (o.getClass() == Integer.class && Integer.class.isAssignableFrom(o.getClass())) {
            document.put(String.valueOf(o), converter.encodeToDocument(value));
          } else {
            document.put((String) o, converter.encodeToDocument(value));
          }
        }

        return document;
      }

      throw new MappingException("Expecting a Map: " + obj.getClass().getName());

    } catch (Throwable t) {
      throw new MappingException("Mapping error", t);
    }
  }

}
