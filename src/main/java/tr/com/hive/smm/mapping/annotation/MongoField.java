package tr.com.hive.smm.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tr.com.hive.smm.mapping2.SimpleMapper;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface MongoField {

  String value() default "";

  /**
   * Please consider using the new {@link SimpleMapper}
   */
  @Deprecated(forRemoval=true, since = "1.0")
  boolean asEmptyArray() default false;

}

