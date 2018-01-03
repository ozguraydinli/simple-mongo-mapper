package tr.com.hive.smm.mapping;

import org.bson.BsonString;
import org.bson.BsonValue;

import tr.com.hive.smm.MapperFactory;

/**
 * Created by ozgur on 4/3/17.
 */
public class StringConverter extends AbstractConverter implements Converter {

  public StringConverter(MapperFactory mapperFactory, String key, Class<?> clazz) {
    super(mapperFactory, key, clazz);
  }

  @Override
  public Object decode(Object obj) {
    if (clazz == String.class && obj instanceof String) {
      return obj;
    } else {
      throw new MappingException("Expecting a String: " + key);
    }
  }

  @Override
  public BsonValue encode(Object obj) throws MappingException {
    return new BsonString((String) obj);
  }

  @Override
  public Object encodeToDocument(Object obj) throws MappingException {
    return obj;
  }

}
