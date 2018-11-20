package tr.com.hive.smm.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReflectionUtils {

  public static List<Field> getAllFields(Class<?> type) {
    List<Field> result = new ArrayList<>();

    Class<?> i = type;
    while (i != null && i != Object.class) {
      Collections.addAll(result, i.getDeclaredFields());
      i = i.getSuperclass();
    }

    return result;
  }

}
