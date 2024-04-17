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

import java.time.Period;

import static java.util.Objects.requireNonNull;

public final class PeriodAsDocumentCodec implements Codec<Period> {

  @Override
  public void encode(BsonWriter writer, Period value, EncoderContext encoderContext) {
    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");

    writer.writeStartDocument();

    writer.writeInt32("years", value.getYears());
    writer.writeInt32("months", value.getMonths());
    writer.writeInt32("days", value.getDays());
    writer.writeInt64("value", Long.parseLong(String.format(
      "%d%02d%02d",
      value.getYears(),
      value.getMonths(),
      value.getDays()
    )));
    writer.writeString("valueString", value.toString()); // ISO-8601

    writer.writeEndDocument();
  }

  @Override
  public Period decode(BsonReader reader, DecoderContext decoderContext) {
    requireNonNull(reader, "reader is null");

    reader.readStartDocument();

    int years = reader.readInt32("years");
    int months = reader.readInt32("months");
    int days = reader.readInt32("days");

    reader.readInt64("value");
    reader.readString("valueString");

    reader.readEndDocument();

    return Period.of(years, months, days);
  }

  @Override
  public Class<Period> getEncoderClass() {
    return Period.class;
  }

}
