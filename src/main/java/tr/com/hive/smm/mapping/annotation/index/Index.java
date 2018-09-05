package tr.com.hive.smm.mapping.annotation.index;


import java.lang.annotation.*;

/**
 * Defines an index
 *
 * @author Scott Hernandez
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface Index {
  /**
   * List of fields to include in the index.  At least one field must be defined unless defining a text index. Use of this field implies
   * use of {@link #options()} and any options defined directly on this annotation will be ignored.
   */
  Field[] fields() default {};

  /**
   * Options to apply to the index.  Use of this field will ignore any of the deprecated options defined on {@link Index} directly.
   */
  IndexOptions options() default @IndexOptions();

  /**
   * Create the index in the background
   *
   * @deprecated use the {@link IndexOptions} found in {@link #options()}
   */
  @Deprecated
  boolean background() default false;

  /**
   * disables validation for the field name
   *
   * @deprecated use the {@link IndexOptions} found in {@link #options()}
   */
  @Deprecated
  boolean disableValidation() default false;

  /**
   * Tells the unique index to drop duplicates silently when creating; only the first will be kept
   *
   * @deprecated use the {@link IndexOptions} found in {@link #options()}
   */
  @Deprecated
  boolean dropDups() default false;

  /**
   * defines the time to live for documents in the collection
   *
   * @deprecated use the {@link IndexOptions} found in {@link #options()}
   */
  @Deprecated
  int expireAfterSeconds() default -1;

  /**
   * The name of the index to create; default is to let the mongodb create a name (in the form of key1_1/-1_key2_1/-1...)
   *
   * @deprecated use the {@link IndexOptions} found in {@link #options()}
   */
  @Deprecated
  String name() default "";

  /**
   * Create the index with the sparse option
   *
   * @deprecated use the {@link IndexOptions} found in {@link #options()}
   */
  @Deprecated
  boolean sparse() default false;

  /**
   * Creates the index as a unique value index; inserting duplicates values in this field will cause errors
   *
   * @deprecated use the {@link IndexOptions} found in {@link #options()}
   */
  @Deprecated
  boolean unique() default false;

  /**
   * List of fields (prepended with "-" for desc; defaults to asc).  If a value is defined for {@link #fields()} this value will be
   * ignored and logged.
   *
   * @deprecated use {@link #fields()}
   */
  @Deprecated
  String value() default "";

}
