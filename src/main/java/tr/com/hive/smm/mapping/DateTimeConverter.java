package tr.com.hive.smm.mapping;

import org.bson.BsonDateTime;
import org.bson.BsonInt32;
import org.bson.BsonValue;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneOffset;
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
    if (!(obj instanceof Date) &&
        !(obj instanceof Integer) &&
        !(obj instanceof BsonDateTime)
    ) {
      throw new MappingException("Expecting a Date: " + key + " " + obj.getClass().getSimpleName());
    }

    if (clazz == Date.class) {
      return obj;
    } else if (clazz == Instant.class) {
      return ((Date) obj).toInstant();
    } else if (clazz == LocalDate.class) {
      if(obj instanceof BsonDateTime) {
        return new Date(((BsonDateTime) obj).getValue()).toInstant().atZone(ZoneOffset.UTC).toLocalDate();
      } else {
        return ((Date) obj).toInstant().atZone(ZoneOffset.UTC).toLocalDate();
      }
    } else if (clazz == Year.class) {
      return Year.of((int) obj);
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
    } else if (LocalDate.class.isAssignableFrom(clzz) || LocalDate.class == clzz) {
      return new BsonDateTime(((LocalDate) obj).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
    } else if (Year.class.isAssignableFrom(clzz) || Year.class == clzz) {
      return new BsonInt32((int) obj);
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
    } else if (obj instanceof LocalDate) {
      return new Date(((LocalDate) obj).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
    } else if (obj instanceof Year) {
      return ((Year) obj).getValue();
    } else {
      throw new MappingException("Expecting a Date: " + key);
    }
  }

}
