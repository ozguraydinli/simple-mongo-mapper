package tr.com.hive.smm.mapping.annotation.index;

import java.lang.annotation.*;

/**
 * Marks this field for inclusion in text indexing.  There can only be on instance of this field on a class/collection due to the server
 * limiting one text index per collection.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Text {
  /**
   * Options to apply to the index
   */
  IndexOptions options() default @IndexOptions;

  /**
   * Weight of the field. If a weight is omitted from this item, the weight is assumed to the database default.
   */
  int value() default -1;
}
