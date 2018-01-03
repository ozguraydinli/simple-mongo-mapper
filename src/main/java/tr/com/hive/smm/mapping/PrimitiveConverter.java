package tr.com.hive.smm.mapping;

import org.bson.*;

import java.util.Date;

import tr.com.hive.smm.MapperFactory;

/**
 * Created by ozgur on 4/3/17.
 */
public class PrimitiveConverter extends AbstractConverter implements Converter {

  public PrimitiveConverter(MapperFactory mapperFactory, String key, Class<?> clazz) {
    super(mapperFactory, key, clazz);
  }

  @Override
  public Object decode(Object obj) {
    return obj;
  }

  @Override
  public BsonValue encode(Object obj) throws MappingException {

    Class<?> clzz = obj.getClass();
    if (Date.class.isAssignableFrom(clzz) || Date.class == clzz) {
      return new BsonDateTime(((Date) obj).getTime());
    } else if (Integer.class.isAssignableFrom(clzz) || int.class == clzz) {
      return new BsonInt32((Integer) obj);
    } else if (Double.class.isAssignableFrom(clzz) || double.class == clzz) {
      return new BsonDouble((Double) obj);
    } else if (Boolean.class.isAssignableFrom(clzz) || boolean.class == clzz) {
      return new BsonBoolean((Boolean) obj);
    } else if (Short.class.isAssignableFrom(clzz) || short.class == clzz) {
      return new BsonInt32((Short) obj);
    } else if (Long.class.isAssignableFrom(clzz) || long.class == clzz) {
      return new BsonInt64((Long) obj);
    } else if (Float.class.isAssignableFrom(clzz) || float.class == clzz) {
      return new BsonDouble((Float) obj);
    } else {
      throw new MappingException("Unkown primitive type: " + clzz.getName());
    }
  }


  @Override
  public Object encodeToDocument(Object obj) throws MappingException {
    return obj;
  }

}
