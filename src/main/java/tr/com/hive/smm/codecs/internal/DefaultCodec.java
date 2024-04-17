package tr.com.hive.smm.codecs.internal;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class DefaultCodec<T> implements Codec<T> {

  protected final Class<?> aClass;

  public DefaultCodec(Class<?> aClass) {
    this.aClass = aClass;
  }

  @Override
  public T decode(BsonReader reader, DecoderContext decoderContext) {
    throw new UnsupportedOperationException("This is a default codec. You should not be using this, please implement a custom codec.");
  }

  @Override
  public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
    throw new UnsupportedOperationException("This is a default codec. You should not be using this, please implement a custom codec.");
  }

  @Override
  public Class<T> getEncoderClass() {
    throw new UnsupportedOperationException("This is a default codec. You should not be using this, please implement a custom codec.");
  }

}
