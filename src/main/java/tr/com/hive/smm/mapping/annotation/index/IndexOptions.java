package tr.com.hive.smm.mapping.annotation.index;

import java.lang.annotation.*;

/**
 * Defines the options to be used when declaring an index.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface IndexOptions {
  /**
   * Create the index in the background
   */
  boolean background() default false;

  /**
   * disables validation for the field name
   */
  boolean disableValidation() default false;

  /**
   * Tells the unique index to drop duplicates silently when creating; only the first will be kept
   *
   * @deprecated Support for this has been removed from the server.  This value is ignored.
   */
  @Deprecated
  boolean dropDups() default false;

  /**
   * defines the time to live for documents in the collection
   */
  int expireAfterSeconds() default -1;

  /**
   * Default language for the index.
   */
  String language() default "";

  /**
   * The field to use to override the default language.
   */
  String languageOverride() default "";

  /**
   * The name of the index to create; default is to let the mongodb create a name (in the form of key1_1/-1_key2_1/-1...)
   */
  String name() default "";

  /**
   * Create the index with the sparse option
   */
  boolean sparse() default false;

  /**
   * Creates the index as a unique value index; inserting duplicates values in this field will cause errors
   */
  boolean unique() default false;

  /**
   * Defines the collation to be used for this index
   * @since 1.3
   */
  String partialFilter() default "";

  /**
   * Defines the collation to be used for this index
   * @since 1.3
   */
  Collation collation() default @Collation(locale = "");
}
