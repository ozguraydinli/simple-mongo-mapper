package tr.com.hive.smm.codecs.time;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.Month;

import static java.util.Objects.requireNonNull;

public class MonthAsDocumentCodec implements Codec<Month> {

  @Override
  public void encode(BsonWriter writer, Month value, EncoderContext encoderContext) {
    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");

    writer.writeStartDocument();

    writer.writeString("monthString", value.name());
    writer.writeInt32("monthInt", value.getValue());

    writer.writeEndDocument();
  }

  @Override
  public Month decode(BsonReader reader, DecoderContext decoderContext) {
    requireNonNull(reader, "reader is null");

    reader.readStartDocument();

    reader.readString("monthString");
    int monthInt = reader.readInt32("monthInt");

    reader.readEndDocument();

    return Month.of(monthInt);
  }

  @Override
  public Class<Month> getEncoderClass() {
    return Month.class;
  }

}
