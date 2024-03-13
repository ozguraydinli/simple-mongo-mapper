package tr.com.hive.smm.mapping2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

import tr.com.hive.smm.model.ClassA;
import tr.com.hive.smm.model.ClassB;
import tr.com.hive.smm.model.ClassWithoutMongoEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleMapperHelperTest {

  @Test
  void getClasses() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    Set<Class<?>> classes = SimpleMapperHelper.getClasses(classLoader, "tr.com.hive.smm");

    assertTrue(classes.contains(ClassA.class));

    Set<Class<?>> classes2 = SimpleMapperHelper.getClasses(classLoader, "tr.com.hive.smm.model");

    assertTrue(classes2.contains(ClassA.class));
    assertTrue(classes2.contains(ClassB.class));
    Assertions.assertFalse(classes2.contains(ClassWithoutMongoEntity.class));
  }

}