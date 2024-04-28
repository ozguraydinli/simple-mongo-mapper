package tr.com.hive.smm.mapping;

import org.bson.BsonDateTime;
import org.bson.BsonValue;

import java.time.Instant;
import java.util.Date;

import tr.com.hive.smm.MapperFactory;

/**
 * Created by ozgur on 4/3/17.
 */
public class DateTimeConverter extends AbstractConverter implements Converter {

  public DateTimeConverter(MapperFactory mapperFactory, String key, Class<?> clazz) {
    super(mapperFactory, key, clazz);
  }

  @Override
  public Object decode(Object obj) {
    if (!(obj instanceof Date)) {
      throw new MappingException("Expecting a Date: " + key);
    }

    if (clazz == Date.class) {
      return obj;
    } else if (clazz == Instant.class) {
      return ((Date) obj).toInstant();
    } else {
      throw new MappingException("Expecting a Date: " + key);
    }
  }

  @Override
  public BsonValue encode(Object obj) throws MappingException {
    Class<?> clzz = obj.getClass();

    if (Date.class.isAssignableFrom(clzz) || Date.class == clzz) {
      return new BsonDateTime(((Date) obj).getTime());
    } else if (Instant.class.isAssignableFrom(clzz) || Instant.class == clzz) {
      return new BsonDateTime(((Instant) obj).toEpochMilli());
    } else {
      throw new MappingException("Unkown date type: " + clzz.getName());
    }
  }

  @Override
  public Object encodeToDocument(Object obj) throws MappingException {
    if (obj == null) {
      return null;
    }

    if (obj instanceof Date) {
      return obj;
    } else if (obj instanceof Instant) {
      return new Date(((Instant) obj).toEpochMilli());
    } else {
      throw new MappingException("Expecting a Date: " + key);
    }
  }

}
