package tr.com.hive.smm.mapping2;

import com.mongodb.MongoClientSettings;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  private SimpleMapper() {
    this.classLoader = Thread.currentThread().getContextClassLoader();
  }

  public static SimpleMapperBuilder builder() {
    return new SimpleMapperBuilder();
  }

  public static CodecRegistry newRegistry(CodecRegistry codecRegistry, Class<?> clazz) {
    return CodecRegistries.fromRegistries(
      codecRegistry,
      CodecRegistries.fromProviders(
        PojoCodecProvider.builder()
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
                                                   .flatMap(p -> SimpleMapperHelper.getClasses(classLoader, p).stream());

    Class<?>[] allClasses = Stream.concat(classesFromPackages, classList.stream())
                                  .collect(Collectors.toUnmodifiableSet())
                                  .toArray(Class<?>[]::new);

    Map<String, MappedClass> mappedClasses = getMappedClasses(allClasses);

    ClassModel<?>[] list = getCustomizedFields(allClasses, mappedClasses);

    codecRegistry = CodecRegistries.fromRegistries(
      MongoClientSettings.getDefaultCodecRegistry(),
      CodecRegistries.fromProviders(
        PojoCodecProvider.builder()
                         .register(allClasses)
                         .register(list)
                         .register(new MapCodecProvider())
                         .build()
      )
    );

    return this;
  }

  private ClassModel<?>[] getCustomizedFields(Class<?>[] array, Map<String, MappedClass> mappedClasses) {
    List<ClassModel<?>> list = new ArrayList<>();

    for (Class<?> aClass : array) {
      Constructor<?>[] declaredConstructors = aClass.getDeclaredConstructors();
      Optional<Constructor<?>> first = Arrays.stream(declaredConstructors)
                                             .filter(c -> c.getParameterCount() == 0)
                                             .peek(c -> c.setAccessible(true))
                                             .findFirst();
      Object dummyInstance = first
        .map(ReflectionUtils::createInstance)
        .orElseThrow(() -> new RuntimeException("Default constructor is mandatory!"));

      String simpleName = aClass.getSimpleName();
      if (!mappedClasses.containsKey(simpleName)) {
        throw new RuntimeException("Unkown class: " + aClass.getName());
      }

      MappedClass mappedClass = mappedClasses.get(simpleName);

      if (mappedClass.shouldBuildClassModel()) {
        ClassModelBuilder<?> classModelBuilder = buildClassModelBuilder(mappedClasses, aClass, mappedClass, dummyInstance);
        list.add(classModelBuilder.build());
      }
    }

    return list.toArray(ClassModel<?>[]::new);
  }

  private static ClassModelBuilder<?> buildClassModelBuilder(Map<String, MappedClass> mappedClasses, Class<?> aClass, MappedClass mappedClass, Object dummyInstance) {
    ClassModelBuilder<?> classModelBuilder = ClassModel.builder(aClass);

    mappedClass.getMongoRefFields().forEach(field -> {
      classModelBuilder.getProperty(field.getName())
                       .codec(new MongoRefCodec<>(mappedClasses, field, dummyInstance));
    });

    mappedClass.getMongoTransientFields().forEach(field -> {
      classModelBuilder.getProperty(field.getName())
                       .readName(null)
                       .writeName(null)
                       .propertySerialization(p -> false);
    });

    mappedClass.getFieldToMongoFieldMap().forEach((field, value) -> {
      classModelBuilder.getProperty(field.getName())
                       .readName(value)
                       .writeName(value);
    });

    mappedClass.getFieldToCodecClassMap().forEach((field, clazz) -> {
      Codec<?> instance = ReflectionUtils.createInstance(clazz, aClass);
      classModelBuilder.getProperty(field.getName())
                       .codec(new CustomCodec<>(aClass, instance));
    });

    return classModelBuilder;
  }

  private Map<String, MappedClass> getMappedClasses(Class<?>[] array) {
    Map<String, MappedClass> map = new HashMap<>();

    for (Class<?> aClass : array) {
      Field[] fields = aClass.getFields();
      MappedClass mappedClass = new MappedClass(aClass);

      for (Field field : fields) {

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

      map.put(aClass.getSimpleName(), mappedClass);
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
