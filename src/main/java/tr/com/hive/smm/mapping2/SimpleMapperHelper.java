package tr.com.hive.smm.mapping2;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.util.ReflectionUtils;

public class SimpleMapperHelper {

  /*
   *
   * Recursively finds all classes under a package name annotated with MongoEntity.class
   * Including nested classes.
   *
   * */
  public static Stream<Class<?>> getClasses(ClassLoader classLoader, String packageName) {
    Reflections reflections = new Reflections(
      new ConfigurationBuilder()
        .forPackage(packageName, classLoader)
        .filterInputsBy(new FilterBuilder().includePackage(packageName))
    );

    return reflections.getTypesAnnotatedWith(MongoEntity.class)
                      .stream()
                      .flatMap(SimpleMapperHelper::getClasses);
  }

  public static Stream<Class<?>> getClasses(Class<?> clazz) {
    return Stream.concat(
      Stream.of(clazz),
      Stream.of(clazz.getDeclaredClasses())
    );
  }

  public static Object createDummyInstance(Class<?> aClass) {
    Constructor<?>[] declaredConstructors = aClass.getDeclaredConstructors();
    Optional<Constructor<?>> first = Arrays.stream(declaredConstructors)
                                           .filter(c -> c.getParameterCount() == 0)
                                           .peek(c -> c.setAccessible(true))
                                           .findFirst();
    return first
      .map(ReflectionUtils::createInstance)
      .orElseThrow(() -> new RuntimeException("Default constructor is mandatory!"));
  }

}
