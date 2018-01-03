package tr.com.hive.smm.mapping;

/**
 * Created by ozgur on 4/4/17.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MongoEntity {

  String value() default "";

}
