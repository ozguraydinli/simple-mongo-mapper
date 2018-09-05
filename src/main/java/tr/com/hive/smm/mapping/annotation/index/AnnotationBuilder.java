package tr.com.hive.smm.mapping.annotation.index;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import tr.com.hive.smm.mapping.MappingException;

import static java.lang.String.format;

abstract class AnnotationBuilder<T extends Annotation> implements Annotation {

  private final Map<String, Object> values = new HashMap<String, Object>();

  AnnotationBuilder() {
    for (Method method : annotationType().getDeclaredMethods()) {
      values.put(method.getName(), method.getDefaultValue());
    }
  }

  AnnotationBuilder(final T original) {
    try {
      for (Method method : annotationType().getDeclaredMethods()) {
        values.put(method.getName(), method.invoke(original));
      }
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  <V> V get(final String key) {
    return (V) values.get(key);
  }

  void put(final String key, final Object value) {
    if (value != null) {
      values.put(key, value);
    }
  }

  void putAll(final Map<String, Object> map) {
    values.putAll(map);
  }

  @Override
  public String toString() {
    return format("@%s %s", annotationType().getName(), values.toString());
  }

  @Override
  public abstract Class<T> annotationType();

  @SuppressWarnings("unchecked")
  static <A extends Annotation> Map<String, Object> toMap(final A annotation) {
    final Map<String, Object> map = new HashMap<String, Object>();
    try {
      Class<A> annotationType = (Class<A>) annotation.annotationType();
      for (Method method : annotationType.getDeclaredMethods()) {
        Object value = method.invoke(annotation);
        if (!method.getDefaultValue().equals(value)) {
          map.put(method.getName(), value);
        }
      }
    } catch (Exception e) {
      throw new MappingException(e.getMessage(), e);
    }
    return map;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AnnotationBuilder)) {
      return false;
    }

    return values.equals(((AnnotationBuilder<?>) o).values);

  }

  @Override
  public int hashCode() {
    return values.hashCode();
  }
}
