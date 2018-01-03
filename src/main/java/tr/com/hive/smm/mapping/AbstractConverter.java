package tr.com.hive.smm.mapping;

import tr.com.hive.smm.MapperFactory;

/**
 * Created by ozgur on 4/7/17.
 */
public abstract class AbstractConverter implements Converter {

  protected MapperFactory mapperFactory;

  protected String key;
  protected Class<?> clazz;

  protected AbstractConverter() {
  }

  public AbstractConverter(MapperFactory mapperFactory, String key, Class<?> clazz) {
    this.key = key;
    this.mapperFactory = mapperFactory;
    this.clazz = clazz;
  }

}
