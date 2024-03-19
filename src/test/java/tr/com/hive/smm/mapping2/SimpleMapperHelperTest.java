package tr.com.hive.smm.mapping2;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import tr.com.hive.smm.model.ClassA;
import tr.com.hive.smm.model.ClassA1;
import tr.com.hive.smm.model.ClassA1.EmbeddedA1WithoutAnnotation;
import tr.com.hive.smm.model.ClassB;
import tr.com.hive.smm.model.ClassWithoutMongoEntity;

import static com.mongodb.assertions.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleMapperHelperTest {

  @Test
  void getClasses() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    Set<Class<?>> classes = SimpleMapperHelper.getClasses(classLoader, "tr.com.hive.smm")
                                                .collect(Collectors.toSet());

    assertTrue(classes.contains(ClassA.class));

    Set<Class<?>> classes2 = SimpleMapperHelper.getClasses(classLoader, "tr.com.hive.smm.model")
                                               .collect(Collectors.toSet());

    assertTrue(classes2.contains(ClassA.class));
    assertTrue(classes2.contains(ClassB.class));
    assertFalse(classes2.contains(ClassWithoutMongoEntity.class));
    assertTrue(classes2.contains(EmbeddedA1WithoutAnnotation.class));

    List<Class<?>> classes3 = SimpleMapperHelper.getClasses(ClassA1.class)
                                                .toList();

    assertEquals(4, classes3.size());

  }

}