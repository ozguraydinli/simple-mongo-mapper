package tr.com.hive.smm.mapping;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by ozgur on 4/7/17.
 */
@SuppressWarnings("unchecked")
public class MapperUtil {

  public static Constructor<?> getDefaultConstructor(Class<?> clazz) {
    if (clazz == null) {
      throw new NullPointerException("clazz cannot be null.");
    }

    // find the default constructor

    // search public constructors
    Optional<Constructor<?>> optional = Stream.of(clazz.getConstructors())
                                              .filter(c -> c.getParameterCount() == 0)
                                              .findFirst();

    if (optional.isPresent()) {
      Constructor<?> defaultConstructor = optional.get();
      defaultConstructor.setAccessible(true);

      return defaultConstructor;
    } else {
      // search non-public constructors
      optional = Stream.of(clazz.getDeclaredConstructors())
                       .filter(c -> c.getParameterCount() == 0)
                       .findFirst();

      if (optional.isPresent()) {
        Constructor<?> defaultConstructor = optional.get();
        defaultConstructor.setAccessible(true);

        return defaultConstructor;
      } else {
        throw new MappingException("No default constructor for " + clazz.getTypeName());
      }
    }
  }

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
