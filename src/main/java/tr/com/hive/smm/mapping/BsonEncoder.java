package tr.com.hive.smm.mapping;

import org.bson.BsonValue;

/**
 * Created by ozgur on 4/7/17.
 */
public interface BsonEncoder {

  BsonValue encode(Object obj) throws MappingException;

}
