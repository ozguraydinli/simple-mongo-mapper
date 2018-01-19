package tr.com.hive.smm.mapping;

import org.bson.BsonDecimal128;
import org.bson.BsonValue;
import org.bson.types.Decimal128;

import java.math.BigDecimal;

import tr.com.hive.smm.MapperFactory;

/**
 * Created by ozgur on 4/3/17.
 */
public class BigDecimalConverter extends AbstractConverter implements Converter {

  public BigDecimalConverter(MapperFactory mapperFactory, String key, Class<?> clazz) {
    super(mapperFactory, key, clazz);
  }

  @Override
  public Object decode(Object obj) {
    if (clazz == BigDecimal.class && obj instanceof Decimal128) {
      return ((Decimal128) obj).bigDecimalValue();
    } else {
      throw new MappingException("Expecting a Decimal128: " + key);
    }
  }

  @Override
  public BsonValue encode(Object obj) throws MappingException {
    return new BsonDecimal128(new Decimal128((BigDecimal) obj));
  }

  @Override
  public Object encodeToDocument(Object obj) throws MappingException {
    return new Decimal128((BigDecimal) obj);
  }

}
