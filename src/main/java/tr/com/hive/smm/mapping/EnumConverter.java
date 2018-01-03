package tr.com.hive.smm.mapping;

import org.bson.BsonString;
import org.bson.BsonValue;

import tr.com.hive.smm.MapperFactory;

/**
 * Created by ozgur on 4/3/17.
 */
@SuppressWarnings("unchecked")
public class EnumConverter extends AbstractConverter implements Converter {

  public EnumConverter(MapperFactory mapperFactory, String key, Class<?> clazz) {
    super(mapperFactory, key, clazz);
  }

  @Override
  public Object decode(Object obj) {
    if (Enum.class.isAssignableFrom(clazz) && obj instanceof String) {
      return Enum.valueOf((Class<Enum>) clazz, (String) obj);
    } else {
      throw new MappingException("Expecting a String");
    }
  }

  @Override
  public BsonValue encode(Object obj) throws MappingException {
    if (obj instanceof Enum) {
      return new BsonString(((Enum) obj).name());
    }

    throw new MappingException("obj must be an Enum");
  }

  @Override
  public Object encodeToDocument(Object obj) throws MappingException {
    if (obj instanceof Enum) {
      return ((Enum) obj).name();
    }

    throw new MappingException("obj must be an Enum");
  }

}
