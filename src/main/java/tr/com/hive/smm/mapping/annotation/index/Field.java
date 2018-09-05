package tr.com.hive.smm.mapping.annotation.index;

import java.lang.annotation.*;

/**
 * Define a field to be used in an index;
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface Field {
  /**
   * "Direction" of the indexing.  Defaults to {@link IndexType#ASC}.
   *
   * @see IndexType
   */
  IndexType type() default IndexType.ASC;

  /**
   * Field name to index
   */
  String value();

  /**
   * The weight to use when creating a text index.  This value only makes sense when direction is {@link IndexType#TEXT}
   */
  int weight() default -1;
}
