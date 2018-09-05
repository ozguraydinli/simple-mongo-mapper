package tr.com.hive.smm.mapping.annotation.index;

@SuppressWarnings("deprecation")
class IndexOptionsBuilder extends AnnotationBuilder<IndexOptions> implements IndexOptions {

  @Override
  public Class<IndexOptions> annotationType() {
    return IndexOptions.class;
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
  public String language() {
    return get("language");
  }

  @Override
  public String languageOverride() {
    return get("languageOverride");
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
  public String partialFilter() {
    return get("partialFilter");
  }

  @Override
  public Collation collation() {
    return get("collation");
  }

  IndexOptionsBuilder background(final boolean background) {
    put("background", background);
    return this;
  }

  IndexOptionsBuilder disableValidation(final boolean disableValidation) {
    put("disableValidation", disableValidation);
    return this;
  }

  IndexOptionsBuilder dropDups(final boolean dropDups) {
    put("dropDups", dropDups);
    return this;
  }

  IndexOptionsBuilder expireAfterSeconds(final int expireAfterSeconds) {
    put("expireAfterSeconds", expireAfterSeconds);
    return this;
  }

  IndexOptionsBuilder language(final String language) {
    put("language", language);
    return this;
  }

  IndexOptionsBuilder languageOverride(final String languageOverride) {
    put("languageOverride", languageOverride);
    return this;
  }

  IndexOptionsBuilder name(final String name) {
    put("name", name);
    return this;
  }

  IndexOptionsBuilder sparse(final boolean sparse) {
    put("sparse", sparse);
    return this;
  }

  IndexOptionsBuilder unique(final boolean unique) {
    put("unique", unique);
    return this;
  }

  IndexOptionsBuilder partialFilter(final String partialFilter) {
    put("partialFilter", partialFilter);
    return this;
  }

  IndexOptionsBuilder collation(final Collation collation) {
    put("collation", collation);
    return this;
  }

  IndexOptionsBuilder migrate(final Index index) {
    putAll(toMap(index));
    return this;
  }

}