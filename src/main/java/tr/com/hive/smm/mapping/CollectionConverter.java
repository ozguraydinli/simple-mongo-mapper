package tr.com.hive.smm.mapping;

import com.google.common.collect.Lists;

import com.mongodb.DBRef;

import org.bson.BsonArray;
import org.bson.BsonValue;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import tr.com.hive.smm.MapperFactory;
import tr.com.hive.smm.mapping.annotation.MongoField;

/**
 * Created by ozgur on 4/3/17.
 */
@SuppressWarnings("unchecked")
public class CollectionConverter extends AbstractConverter implements Converter {

  private ParameterizedType genericType;
  private MappedField mappedField;

  public CollectionConverter(MapperFactory mapperFactory, String key, Class<?> clazz, ParameterizedType genericType, MappedField mappedField) {
    super(mapperFactory, key, clazz);
    this.genericType = genericType;
    this.mappedField = mappedField;
  }

  @Override
  public Object decode(Object obj) {
    try {

      Collection<Object> collection;

      if (clazz.isInterface()) {
        if (clazz == List.class) {
          collection = MapperUtil.newInstance(clazz, ArrayList.class);
        } else if (clazz == Set.class) {
          collection = MapperUtil.newInstance(clazz, HashSet.class);
        } else {
          throw new IllegalStateException("Unknown collection type : " + clazz.getName());
        }
      } else {
        collection = (Collection<Object>) clazz.newInstance();
      }

      Class<?> cls2;
      Type[] actualTypeArguments = genericType.getActualTypeArguments();
      Type typeArgument2 = actualTypeArguments[0];
      if (typeArgument2 instanceof ParameterizedType) {
        cls2 = Class.forName(((ParameterizedType) typeArgument2).getRawType().getTypeName());
      } else {
        cls2 = Class.forName(typeArgument2.getTypeName());
      }

      MappedField mappedField = new MappedField(cls2, typeArgument2);

      ArrayList arrayList = (ArrayList) obj;
      for (Object o : arrayList) {
        if (o instanceof DBRef) {
          mappedField.setRef(true);
        }

        Converter converter = mapperFactory.get(key, o, mappedField);

        collection.add(converter.decode(o));
      }

      return collection;

    } catch (Throwable t) {
      throw new MappingException("Mapping error", t);
    }
  }

  @Override
  public BsonArray encode(Object obj) throws MappingException {
    try {
      if (Collection.class.isAssignableFrom(obj.getClass())) {
        Class<?> cls2;
        Type[] actualTypeArguments = genericType.getActualTypeArguments();
        Type typeArgument2 = actualTypeArguments[0];
        if (typeArgument2 instanceof ParameterizedType) {
          cls2 = Class.forName(((ParameterizedType) typeArgument2).getRawType().getTypeName());
        } else {
          cls2 = Class.forName(typeArgument2.getTypeName());
        }

        Collection collection = (Collection) obj;

        if (collection.size() == 0) {
          return null;
        }

        BsonArray bsonArray = new BsonArray();

        for (Object o : collection) {

          MappedField mappedFieldInner = new MappedField(cls2, typeArgument2);
          if (mappedField.isRef()) {
            mappedFieldInner.setRef(true);
          }

          if (mappedField.hasCustomConverter()) {
            mappedFieldInner.setHasCustomConverter(true);
            mappedFieldInner.setCustomConverter(mappedField.getConverterClazz());
          }

          Converter converter = mapperFactory.get(key, o, mappedFieldInner);

          BsonValue encode = converter.encode(o);

          if (encode != null) {
            bsonArray.add(encode);
          }
        }

        return bsonArray;
      }

      return null;

    } catch (Throwable t) {
      throw new MappingException("Mapping error", t);
    }
  }

  @Override
  public Object encodeToDocument(Object obj) throws MappingException {
    try {
      if (Collection.class.isAssignableFrom(obj.getClass())) {
        Class<?> cls2;
        Type[] actualTypeArguments = genericType.getActualTypeArguments();
        Type typeArgument2 = actualTypeArguments[0];
        if (typeArgument2 instanceof ParameterizedType) {
          cls2 = Class.forName(((ParameterizedType) typeArgument2).getRawType().getTypeName());
        } else {
          cls2 = Class.forName(typeArgument2.getTypeName());
        }

        Collection collection = (Collection) obj;

        MongoField mongoField = mappedField.getMongoField();
        if (mongoField == null || !mongoField.asEmptyArray()) {
          if (collection.size() == 0) {
            return null;
          }
        }

//        if (mongoField != null) {
//          if (!mongoField.asEmptyArray()) {
//            if (collection.size() == 0) {
//              return null;
//            }
//          }
//        } else {
//          if (collection.size() == 0) {
//            return null;
//          }
//        }

        ArrayList<Object> list = Lists.newArrayList();

        for (Object o : collection) {

          MappedField mappedFieldInner = new MappedField(cls2, typeArgument2);
          if (mappedField.isRef()) {
            mappedFieldInner.setRef(true);
          }

          if (mappedField.hasCustomConverter()) {
            mappedFieldInner.setHasCustomConverter(true);
            mappedFieldInner.setCustomConverter(mappedField.getConverterClazz());
          }

          Converter converter = mapperFactory.get(key, o, mappedFieldInner);

          Object encodeToDocument = converter.encodeToDocument(o);
          if (encodeToDocument != null) {
            list.add(encodeToDocument);
          }
        }

        return list;
      }

      return null;

    } catch (Throwable t) {
      throw new MappingException("Mapping error", t);
    }
  }

}
