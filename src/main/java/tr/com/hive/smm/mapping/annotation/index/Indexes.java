package tr.com.hive.smm.mapping.annotation.index;

import java.lang.annotation.*;

/**
 * Defines indexes for this entity type (on the collection)
 *
 * @author Scott Hernandez
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Indexes {
  /**
   * The index definitions for this entity
   */
  Index[] value();
}
