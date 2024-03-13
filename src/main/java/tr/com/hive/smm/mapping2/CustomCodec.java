package tr.com.hive.smm.mapping2;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

@SuppressWarnings("unchecked")
public class CustomCodec<T> implements Codec<T> {

  private final Class<?> aClass;
  private final Codec<T> codec;

  public CustomCodec(Class<?> aClass, Codec<?> codec) {
    this.aClass = aClass;
    this.codec = (Codec<T>) codec;
  }

  @Override
  public T decode(BsonReader reader, DecoderContext decoderContext) {
    return codec.decode(reader, decoderContext);
  }

  @Override
  public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
    codec.encode(writer, value, encoderContext);
  }

  @Override
  public Class<T> getEncoderClass() {
    return (Class<T>) aClass;
  }

}
