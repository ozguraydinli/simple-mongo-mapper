package tr.com.hive.smm.mapping.annotation.index;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class IndexBuilder extends AnnotationBuilder<Index> implements Index {

  IndexBuilder() {
  }

  IndexBuilder(final Index original) {
    super(original);
  }

  public static Index normalize(final Index index) {
    return index.fields().length != 0
           ? index
           : new IndexBuilder()
             .migrate(index);
  }

  @Override
  public Class<Index> annotationType() {
    return Index.class;
  }

  public IndexBuilder fields(final String fields) {
    return fields(parseFieldsString(fields));
  }

  @Override
  public Field[] fields() {
    return get("fields");
  }

  @Override
  public IndexOptions options() {
    return get("options");
  }

  @Override
  public boolean background() {
    return get("background");
  }

  @Override
  public boolean disableValidation() {
    return get("disableValidation");
  }

  @Override
  public boolean dropDups() {
    return get("dropDups");
  }

  @Override
  public int expireAfterSeconds() {
    return get("expireAfterSeconds");
  }

  @Override
  public String name() {
    return get("name");
  }

  @Override
  public boolean sparse() {
    return get("sparse");
  }

  @Override
  public boolean unique() {
    return get("unique");
  }

  @Override
  public String value() {
    return get("value");
  }

  private List<Field> parseFieldsString(final String str) {
    List<Field> fields = new ArrayList<Field>();
    final String[] parts = str.split(",");
    for (String s : parts) {
      s = s.trim();
      IndexType dir = IndexType.ASC;

      if (s.startsWith("-")) {
        dir = IndexType.DESC;
        s = s.substring(1).trim();
      }

      fields.add(new FieldBuilder()
                   .value(s)
                   .type(dir));
    }
    return fields;
  }

  IndexBuilder fields(final List<Field> fields) {
    put("fields", fields.toArray(new Field[0]));
    return this;
  }

  IndexBuilder fields(final Field... fields) {
    put("fields", fields);
    return this;
  }

  /**
   * Options to apply to the index.  Use of this field will ignore any of the deprecated options defined on {@link Index} directly.
   */
  IndexBuilder options(final IndexOptions options) {
    put("options", options);
    return this;
  }

  /**
   * Create the index in the background
   */
  IndexBuilder background(final boolean background) {
    put("background", background);
    return this;
  }

  /**
   * disables validation for the field name
   */
  IndexBuilder disableValidation(final boolean disableValidation) {
    put("disableValidation", disableValidation);
    return this;
  }

  /**
   * Tells the unique index to drop duplicates silently when creating; only the first will be kept
   */
  IndexBuilder dropDups(final boolean dropDups) {
    put("dropDups", dropDups);
    return this;
  }

  /**
   * defines the time to live for documents in the collection
   */
  IndexBuilder expireAfterSeconds(final int expireAfterSeconds) {
    put("expireAfterSeconds", expireAfterSeconds);
    return this;
  }

  /**
   * The name of the index to create; default is to let the mongodb create a name (in the form of key1_1/-1_key2_1/-1...)
   */
  IndexBuilder name(final String name) {
    put("name", name);
    return this;
  }

  /**
   * Create the index with the sparse option
   */
  IndexBuilder sparse(final boolean sparse) {
    put("sparse", sparse);
    return this;
  }

  /**
   * Creates the index as a unique value index; inserting duplicates values in this field will cause errors
   */
  IndexBuilder unique(final boolean unique) {
    put("unique", unique);
    return this;
  }

  /**
   * List of fields (prepended with "-" for desc; defaults to asc).
   */
  IndexBuilder value(final String value) {
    put("value", value);
    return this;
  }

  IndexBuilder migrate(final Index index) {
    return fields(parseFieldsString(index.value()))
      .options(new IndexOptionsBuilder().migrate(index));
  }
}
