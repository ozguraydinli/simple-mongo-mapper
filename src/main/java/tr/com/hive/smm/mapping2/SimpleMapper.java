package tr.com.hive.smm.mapping2;

import com.mongodb.MongoClientSettings;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tr.com.hive.smm.codecs.BigIntegerCodecProvider;
import tr.com.hive.smm.codecs.internal.MapCodecProvider;
import tr.com.hive.smm.codecs.time.JavaTimeCodecProvider;
import tr.com.hive.smm.mapping.annotation.MongoCustomConverter;
import tr.com.hive.smm.mapping.annotation.MongoField;
import tr.com.hive.smm.mapping.annotation.MongoId;
import tr.com.hive.smm.mapping.annotation.MongoRef;
import tr.com.hive.smm.mapping.annotation.MongoTransient;
import tr.com.hive.smm.util.ReflectionUtils;

public class SimpleMapper {

  private final List<String> packages = new ArrayList<>();
  private final List<Class<?>> classList = new ArrayList<>();

  private final ClassLoader classLoader;

  private CodecRegistry codecRegistry;

  private Map<String, MappedClass> mappedClasses;

  private SimpleMapper() {
    this.classLoader = Thread.currentThread().getContextClassLoader();
  }

  public static SimpleMapperBuilder builder() {
    return new SimpleMapperBuilder();
  }

  @SuppressWarnings("unused")
  public CodecRegistry createRegistry(CodecRegistry codecRegistry, Class<?>... clazz) {
    return CodecRegistries.fromRegistries(
      codecRegistry,
      CodecRegistries.fromProviders(
        PojoCodecProvider.builder()
                         .automatic(true)
                         .conventions(List.of(new SimpleMapperDefaultConvention(mappedClasses), Conventions.ANNOTATION_CONVENTION, Conventions.OBJECT_ID_GENERATORS))
                         .register(clazz)
                         .build()
      )
    );
  }

  public CodecRegistry getCodecRegistry() {
    Objects.requireNonNull(codecRegistry, "CodecRegistry not ready to use! Please build it first!");
    return codecRegistry;
  }

  protected SimpleMapper create() {
    Stream<Class<?>> classesFromPackages = packages.stream()
                                                   .flatMap(p -> SimpleMapperHelper.getClasses(classLoader, p));

    Stream<Class<?>> classesFromClassList = classList.stream()
                                                     .flatMap(SimpleMapperHelper::getClasses);

    Class<?>[] allClasses = Stream.concat(classesFromPackages, classesFromClassList)
                                  .collect(Collectors.toUnmodifiableSet())
                                  .toArray(Class<?>[]::new);

    mappedClasses = getMappedClasses(allClasses);

    codecRegistry = CodecRegistries.fromRegistries(
      CodecRegistries.fromProviders(List.of(new BigIntegerCodecProvider(), new JavaTimeCodecProvider())),
      MongoClientSettings.getDefaultCodecRegistry(),
      CodecRegistries.fromProviders(
        PojoCodecProvider.builder()
                         .automatic(true)
                         .conventions(List.of(new SimpleMapperDefaultConvention(mappedClasses), Conventions.ANNOTATION_CONVENTION, Conventions.OBJECT_ID_GENERATORS))
                         .register(allClasses)
                         .register(new MapCodecProvider())
                         .build()
      )
    );

    return this;
  }

  public List<? extends Class<?>> getMappedClasses() {
    return mappedClasses.values()
                        .stream()
                        .map(MappedClass::getMappedClass)
                        .toList();
  }

  private Map<String, MappedClass> getMappedClasses(Class<?>[] array) {
    Map<String, MappedClass> map = new HashMap<>();

    for (Class<?> aClass : array) {
      MappedClass mappedClass = new MappedClass(aClass);

      List<Field> allFields = ReflectionUtils.getAllFields(aClass)
                                             .stream()
                                             .filter(field -> !field.isSynthetic())
                                             .filter(field -> !Modifier.isStatic(field.getModifiers()))
                                             .filter(field -> !Modifier.isFinal(field.getModifiers()))
                                             .toList();

      for (Field field : allFields) {
        if (field.isAnnotationPresent(MongoId.class)) {
          mappedClass.setIdField(field);
        } else if (field.isAnnotationPresent(MongoRef.class)) {
          mappedClass.addMongoRefField(field);
        } else if (field.isAnnotationPresent(MongoTransient.class)) {
          mappedClass.addMongoTransientField(field);
        } else if (field.isAnnotationPresent(MongoField.class)) {
          MongoField annotation = field.getAnnotation(MongoField.class);
          String value = annotation.value();
          mappedClass.addMongoField(field, value);
        } else if (field.isAnnotationPresent(MongoCustomConverter.class)) {
          MongoCustomConverter annotation = field.getAnnotation(MongoCustomConverter.class);
          Class<?> codecClass = annotation.codec();
          mappedClass.addCustomConverter(field, codecClass);
        }
      }

      map.put(aClass.getName(), mappedClass);
    }

    return map;
  }

  public static final class SimpleMapperBuilder {

    private final List<String> packages = new ArrayList<>();
    private final List<Class<?>> classList = new ArrayList<>();

    public SimpleMapperBuilder forClass(Class<?> clazz) {
      classList.add(clazz);
      return this;
    }

    public SimpleMapperBuilder forPackage(String packageName) {
      packages.add(packageName);
      return this;
    }

    public SimpleMapper build() {
      SimpleMapper simpleMapper = new SimpleMapper();
      simpleMapper.packages.addAll(packages);
      simpleMapper.classList.addAll(classList);

      return simpleMapper.create();
    }

  }

}
