package tr.com.hive.smm.mapping;

import java.lang.reflect.Constructor;

/**
 * Created by ozgur on 4/7/17.
 */
@SuppressWarnings("unchecked")
public class MapperUtil {

  public static <T> T newInstance(final Class<?> clazz, final Class<T> fallbackClazz) throws IllegalAccessException, InstantiationException {
    if (clazz != null) {
      try {
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return (T) constructor.newInstance();
      } catch (Exception e) {
        if (fallbackClazz != null) {
          return fallbackClazz.newInstance();
        }

        return null;
      }
    }

    if (fallbackClazz != null) {
      return fallbackClazz.newInstance();
    }

    return null;
  }

}
