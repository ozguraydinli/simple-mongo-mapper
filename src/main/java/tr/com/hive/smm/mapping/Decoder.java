package tr.com.hive.smm.mapping;

/**
 * Created by ozgur on 4/7/17.
 */
public interface Decoder {

  Object decode(Object obj) throws MappingException;

}
