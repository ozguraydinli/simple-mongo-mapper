package tr.com.hive.smm;

import com.google.common.collect.Lists;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.Document;
import org.bson.codecs.Encoder;
import org.bson.codecs.EncoderContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

import tr.com.hive.smm.mapping.annotation.index.*;

public class IndexHelper {

  private static final EncoderContext ENCODER_CONTEXT = EncoderContext.builder().build();

  private MongoDatabase mongoDatabase;

  public IndexHelper(MongoDatabase mongoDatabase) {
    this.mongoDatabase = mongoDatabase;
  }

  public void createIndexes(MappedClass mappedClass) {
    if (mongoDatabase == null) {
      return;
    }

    MongoCollection<Document> collection = mongoDatabase.getCollection(mappedClass.getCollectionName());

    List<Index> indexes = mappedClass.getIndexes();
    for (Index index : indexes) {
      createIndex(collection, index);
    }
  }

  private void createIndex(final MongoCollection collection, final Index index) {
    Index normalized = IndexBuilder.normalize(index);
//    Index normalized = index;

    BsonDocument keys = calculateKeys(normalized);
    com.mongodb.client.model.IndexOptions indexOptions = convert(normalized.options());
    calculateWeights(normalized, indexOptions);

    collection.createIndex(keys, indexOptions);
  }

  private void calculateWeights(final Index index, final com.mongodb.client.model.IndexOptions indexOptions) {
    Document weights = new Document();
    for (Field field : index.fields()) {
      if (field.weight() != -1) {
        if (field.type() == IndexType.TEXT) {
          weights.put(field.value(), field.weight());
        }
      }
    }
    if (!weights.isEmpty()) {
      indexOptions.weights(weights);
    }
  }

  @SuppressWarnings("deprecation")
  private com.mongodb.client.model.IndexOptions convert(final IndexOptions options) {
    if (options.dropDups()) {
//      LOG.warning("Support for dropDups has been removed from the server.  Please remove this setting.");
    }

    com.mongodb.client.model.IndexOptions indexOptions = new com.mongodb.client.model.IndexOptions()
      .background(options.background())
      .sparse(options.sparse())
      .unique(options.unique());

    if (!options.language().equals("")) {
      indexOptions.defaultLanguage(options.language());
    }
    if (!options.languageOverride().equals("")) {
      indexOptions.languageOverride(options.languageOverride());
    }
    if (!options.name().equals("")) {
      indexOptions.name(options.name());
    }
    if (options.expireAfterSeconds() != -1) {
      indexOptions.expireAfter((long) options.expireAfterSeconds(), TimeUnit.SECONDS);
    }
    if (!options.partialFilter().equals("")) {
      indexOptions.partialFilterExpression(Document.parse(options.partialFilter()));
    }
    if (!options.collation().locale().equals("")) {
      indexOptions.collation(convert(options.collation()));
    }

    return indexOptions;
  }

  private com.mongodb.client.model.Collation convert(final Collation collation) {
    return com.mongodb.client.model.Collation.builder()
                                             .locale(collation.locale())
                                             .backwards(collation.backwards())
                                             .caseLevel(collation.caseLevel())
                                             .collationAlternate(collation.alternate())
                                             .collationCaseFirst(collation.caseFirst())
                                             .collationMaxVariable(collation.maxVariable())
                                             .collationStrength(collation.strength())
                                             .normalization(collation.normalization())
                                             .numericOrdering(collation.numericOrdering())
                                             .build();
  }

  private BsonDocument calculateKeys(final Index index) {
    BsonDocument keys = new BsonDocument();

    for (Field field : index.fields()) {
      String path = String.join(".", Lists.newArrayList(field.value().split("\\.")));
      keys.putAll(toBsonDocument(path, field.type().toIndexValue()));
    }

    return keys;
  }

  @SuppressWarnings("unchecked")
  private BsonDocument toBsonDocument(final String key, final Object value) {
    BsonDocumentWriter writer = new BsonDocumentWriter(new BsonDocument());
    writer.writeStartDocument();
    writer.writeName(key);
    ((Encoder) mongoDatabase.getCodecRegistry().get(value.getClass())).encode(writer, value, ENCODER_CONTEXT);
    writer.writeEndDocument();
    return writer.getDocument();
  }

}
