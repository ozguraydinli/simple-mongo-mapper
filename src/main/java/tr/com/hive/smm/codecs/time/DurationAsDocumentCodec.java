/*
 * Copyright (c)  Hive A.S. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by ismet, 2024
 */

package tr.com.hive.smm.codecs.time;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.Decimal128;

import java.time.Duration;

import static java.util.Objects.requireNonNull;

public class DurationAsDocumentCodec implements Codec<Duration> {

  @Override
  public void encode(BsonWriter writer, Duration value, EncoderContext encoderContext) {
    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");

    writer.writeStartDocument();

    writer.writeInt64("seconds", value.getSeconds());
    writer.writeInt32("nanos", value.getNano());
    writer.writeDecimal128("value", Decimal128.parse(String.format(
      "%d.%09d",
      value.getSeconds(),
      value.getNano()
    )));
    writer.writeString("valueString", value.toString()); // ISO-8601

    writer.writeEndDocument();
  }

  @Override
  public Duration decode(BsonReader reader, DecoderContext decoderContext) {
    requireNonNull(reader, "reader is null");

    reader.readStartDocument();

    long seconds = reader.readInt64("seconds");
    int nanos = reader.readInt32("nanos");
    reader.readDecimal128("value");
    reader.readString("valueString");

    reader.readEndDocument();

    return Duration.ofSeconds(seconds, nanos);
  }

  @Override
  public Class<Duration> getEncoderClass() {
    return Duration.class;
  }

}
