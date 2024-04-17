package tr.com.hive.smm.mapping2;

import org.bson.codecs.Codec;
import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.PropertyModelBuilder;

import java.lang.reflect.Field;
import java.util.Map;

import tr.com.hive.smm.codecs.internal.CustomCodec;
import tr.com.hive.smm.codecs.internal.MongoRefCodec;
import tr.com.hive.smm.mapping.annotation.MongoCustomConverter;
import tr.com.hive.smm.mapping.annotation.MongoField;
import tr.com.hive.smm.mapping.annotation.MongoId;
import tr.com.hive.smm.mapping.annotation.MongoRef;
import tr.com.hive.smm.mapping.annotation.MongoTransient;
import tr.com.hive.smm.util.ReflectionUtils;

public class SimpleMapperDefaultConvention implements Convention {

  private final Map<String, MappedClass> mappedClasses;

  public SimpleMapperDefaultConvention(Map<String, MappedClass> mappedClasses) {
    this.mappedClasses = mappedClasses;
  }

  @Override
  public void apply(final ClassModelBuilder<?> classModelBuilder) {
    if (classModelBuilder.getDiscriminatorKey() == null) {
      classModelBuilder.discriminatorKey("_t");
    }

    if (classModelBuilder.getDiscriminator() == null && classModelBuilder.getType() != null) {
      classModelBuilder.discriminator(classModelBuilder.getType().getName());
    }

    for (PropertyModelBuilder<?> propertyModelBuilder : classModelBuilder.getPropertyModelBuilders()) {
      processPropertyAnnotations(classModelBuilder, propertyModelBuilder);
    }
  }

  private void processPropertyAnnotations(final ClassModelBuilder<?> classModelBuilder, final PropertyModelBuilder<?> propertyModelBuilder) {
    String propertyName = propertyModelBuilder.getName();

    propertyModelBuilder.getReadAnnotations().forEach(annotation -> {
      switch (annotation) {
        case MongoId __ -> classModelBuilder.idPropertyName(propertyName);
        case MongoTransient __ -> doMongoTransient(classModelBuilder, propertyName);
        case MongoRef __ -> doMongoRef(classModelBuilder, propertyName, mappedClasses);
        case MongoField mongoField -> doMongoField(classModelBuilder, propertyName, mongoField);
        case MongoCustomConverter mongoCustomConverter -> doMongoCustomConverter(classModelBuilder, propertyName, mongoCustomConverter);
        default -> doNothing();
      }
    });
  }

  private static void doMongoCustomConverter(ClassModelBuilder<?> classModelBuilder, String propertyName, MongoCustomConverter mongoCustomConverter) {
    Class<?> type = classModelBuilder.getType();

    if (mongoCustomConverter.codec() != null) {
      Codec<?> instance = ReflectionUtils.createInstance(mongoCustomConverter.codec(), type);
      classModelBuilder.getProperty(propertyName)
                       .codec(new CustomCodec<>(type, instance));
    }
  }

  private static void doMongoTransient(ClassModelBuilder<?> classModelBuilder, String propertyName) {
    classModelBuilder.getProperty(propertyName)
                     .readName(null)
                     .writeName(null)
                     .propertySerialization(p -> false);
  }

  private static void doMongoField(ClassModelBuilder<?> classModelBuilder, String propertyName, MongoField mongoField) {
    String value = mongoField.value();

    if (!"".equals(value)) {
      classModelBuilder.getProperty(propertyName)
                       .readName(value)
                       .writeName(value);
    }
  }

  private static void doMongoRef(ClassModelBuilder<?> classModelBuilder, String propertyName, Map<String, MappedClass> mappedClasses) {
    Class<?> type = classModelBuilder.getType();
    Object dummyInstance = SimpleMapperHelper.createDummyInstance(type);

    try {
      Field field = dummyInstance.getClass().getDeclaredField(propertyName);

      classModelBuilder.getProperty(propertyName)
                       .codec(new MongoRefCodec<>(mappedClasses, field, dummyInstance));
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  private static void doNothing() {

  }

}
