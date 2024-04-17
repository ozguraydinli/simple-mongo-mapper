package tr.com.hive.smm.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.Decimal128;

import java.math.BigInteger;

public class BigIntegerCodec implements Codec<BigInteger> {


  @Override
  public void encode(BsonWriter writer, BigInteger value, EncoderContext encoderContext) {
    writer.writeDecimal128(Decimal128.parse(value.toString()));
  }

  @Override
  public BigInteger decode(BsonReader reader, DecoderContext decoderContext) {
    return reader.readDecimal128().bigDecimalValue().toBigInteger();
  }

  @Override
  public Class<BigInteger> getEncoderClass() {
    return BigInteger.class;
  }

}
