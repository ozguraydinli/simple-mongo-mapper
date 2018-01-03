package tr.com.hive.smm.mapping;

import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.types.ObjectId;

import tr.com.hive.smm.MapperFactory;

/**
 * Created by ozgur on 4/3/17.
 */
public class ObjectIdConverter extends AbstractConverter implements Converter {

  public ObjectIdConverter(MapperFactory mapperFactory, String key, Class<?> clazz) {
    super(mapperFactory, key, clazz);
  }

  @Override
  public Object decode(Object obj) {
    if (clazz == ObjectId.class && obj instanceof ObjectId) {
      return obj;
    } else {
      throw new MappingException("Expecting an ObjectId: " + key);
    }
  }

  @Override
  public BsonValue encode(Object obj) throws MappingException {
    return new BsonObjectId((ObjectId) obj);
  }

  @Override
  public Object encodeToDocument(Object obj) throws MappingException {
    return obj;
  }

}
