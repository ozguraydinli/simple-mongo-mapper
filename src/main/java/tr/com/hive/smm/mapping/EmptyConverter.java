package tr.com.hive.smm.mapping;

import org.bson.BsonValue;

/**
 * Created by ozgur on 4/3/17.
 */
@SuppressWarnings("unchecked")
public class EmptyConverter extends AbstractConverter implements Converter {

  public EmptyConverter() {
  }

  @Override
  public Object decode(Object obj) {
    return null;
  }

  @Override
  public BsonValue encode(Object obj) throws MappingException {
    return null;
  }

  @Override
  public Object encodeToDocument(Object obj) throws MappingException {
    return null;
  }

}
