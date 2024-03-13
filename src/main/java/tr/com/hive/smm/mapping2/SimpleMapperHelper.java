package tr.com.hive.smm.mapping2;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.util.Set;

import tr.com.hive.smm.mapping.annotation.MongoEntity;

public class SimpleMapperHelper {

  /*
   *
   * Recursively finds all classes under a package name annotated with MongoEntity.class
   * Including nested classes.
   *
   * */
  public static Set<Class<?>> getClasses(ClassLoader classLoader, String packageName) {
    Reflections reflections = new Reflections(
      new ConfigurationBuilder()
        .forPackage(packageName, classLoader)
    );

    return reflections.getTypesAnnotatedWith(MongoEntity.class);
  }

}
