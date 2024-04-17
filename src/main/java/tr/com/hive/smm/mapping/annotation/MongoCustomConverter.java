package tr.com.hive.smm.mapping.annotation;

/**
 * Created by ozgur on 4/4/17.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tr.com.hive.smm.mapping.Converter;
import tr.com.hive.smm.mapping.EmptyConverter;
import tr.com.hive.smm.codecs.internal.DefaultCodec;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface MongoCustomConverter {

  Class<? extends Converter> value() default EmptyConverter.class;

  Class<?> converterClass() default EmptyConverter.class;

  Class<?> codec() default DefaultCodec.class;

}
