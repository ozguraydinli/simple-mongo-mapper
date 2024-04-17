package tr.com.hive.smm.codecs;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.math.BigInteger;

public class BigIntegerCodecProvider implements CodecProvider {

  @SuppressWarnings("unchecked")
  @Override
  public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
    if (clazz == BigInteger.class) {
      return (Codec<T>) new BigIntegerCodec();
    }

    return null;
  }

}
