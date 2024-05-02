package tr.com.hive.smm.codecs.time;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.Month;

import static java.util.Objects.requireNonNull;

public class MonthAsInt32Codec implements Codec<Month> {

  @Override
  public void encode(BsonWriter writer, Month value, EncoderContext encoderContext) {
    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");

    writer.writeInt32(value.getValue());
  }

  @Override
  public Month decode(BsonReader reader, DecoderContext decoderContext) {
    requireNonNull(reader, "reader is null");

    int monthInt = reader.readInt32();

    return Month.of(monthInt);
  }

  @Override
  public Class<Month> getEncoderClass() {
    return Month.class;
  }

}
