package tr.com.hive.smm.codecs.internal;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.pojo.TypeWithTypeParameters;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MapCodec<K, T> implements Codec<Map<K, T>> {

  private final Class<Map<K, T>> encoderClass;
  private final Codec<T> valueCodec;
  private final TypeWithTypeParameters<?> keyType;

  public MapCodec(final Class<Map<K, T>> encoderClass, final Codec<T> valueCodec, final TypeWithTypeParameters<?> keyType) {
    this.encoderClass = encoderClass;
    this.valueCodec = valueCodec;
    this.keyType = keyType;
  }

  @Override
  public void encode(final BsonWriter writer, final Map<K, T> map, final EncoderContext encoderContext) {
    writer.writeStartDocument();

    for (Map.Entry<K, T> entry : map.entrySet()) {
      writeKey(writer, entry);

      if (entry.getValue() == null) {
        writer.writeNull();
      } else {
        valueCodec.encode(writer, entry.getValue(), encoderContext);
      }
    }

    writer.writeEndDocument();
  }

  private void writeKey(BsonWriter writer, Entry<K, T> entry) {
    if (getKeyClass().isEnum()) {
      writer.writeName(((Enum<?>) entry.getKey()).name());

    } else if (isPrimitive(getKeyClass())) {
      writer.writeName(entry.getKey() + "");

    } else if (getKeyClass() == ObjectId.class) {
      writer.writeName(((ObjectId) entry.getKey()).toString());

    } else {
      writer.writeName(entry.getKey().toString());
    }
  }

  private Class<?> getKeyClass() {
    return keyType.getType();
  }

  protected boolean isPrimitive(Class<?> clazz) {
    return clazz == Integer.class || clazz == Long.class || clazz == Double.class || clazz == Character.class;
  }

  @Override
  public Map<K, T> decode(final BsonReader reader, final DecoderContext context) {
    reader.readStartDocument();
    Map<K, T> map = getInstance();

    while (BsonType.END_OF_DOCUMENT != reader.readBsonType()) {
      K key = readKey(reader);

      if (reader.getCurrentBsonType() == BsonType.NULL) {
        map.put(key, null);
        reader.readNull();
      } else {
        map.put(key, valueCodec.decode(reader, context));
      }
    }

    reader.readEndDocument();

    return map;
  }

  @SuppressWarnings("unchecked")
  protected K readKey(BsonReader reader) {
    if (getKeyClass().isEnum()) {
      // do not delete the cast
      return (K) Enum.valueOf(getKeyClass().asSubclass(Enum.class), reader.readName());
    } else if (getKeyClass() == Integer.class) {
      return (K) Integer.valueOf(reader.readName());
    } else if (getKeyClass() == Long.class) {
      return (K) Long.valueOf(reader.readName());
    } else if (getKeyClass() == Double.class) {
      return (K) Double.valueOf(reader.readName());
    } else if (getKeyClass() == Character.class) {
      return (K) Character.valueOf(reader.readName().charAt(0));
    } else if (getKeyClass() == ObjectId.class) {
      return (K) new ObjectId(reader.readName());
    }

    return (K) reader.readName();
  }

  @Override
  public Class<Map<K, T>> getEncoderClass() {
    return encoderClass;
  }

  private Map<K, T> getInstance() {
    if (encoderClass.isInterface()) {
      return new HashMap<>();
    }

    try {
      return encoderClass.getDeclaredConstructor().newInstance();
    } catch (final Exception e) {
      throw new CodecConfigurationException(e.getMessage(), e);
    }
  }

}