package tr.com.hive.smm.util;

import org.bson.codecs.Codec;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReflectionUtils {

  public static Object createInstance(Constructor<?> constructor) {
    try {
     return  constructor.newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static Codec<?> createInstance(Class<?> clazz, Class<?> aClass) {
    try {

      return (Codec<?>) clazz.getDeclaredConstructor(Class.class).newInstance(aClass);

    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T, V> void setFieldValue(Field field, T obj, V value) {
    field.setAccessible(true);
    try {
      field.set(obj, value);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T, R> R getFieldValue(Field field, T obj) {
    field.setAccessible(true);

    try {

      return (R) field.get(obj);

    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

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
