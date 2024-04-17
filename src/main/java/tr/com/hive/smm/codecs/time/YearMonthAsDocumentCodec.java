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

import java.time.YearMonth;

import static java.util.Objects.requireNonNull;

public final class YearMonthAsDocumentCodec implements Codec<YearMonth> {

  @Override
  public void encode(BsonWriter writer, YearMonth value, EncoderContext encoderContext) {
    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");

    writer.writeStartDocument();
    writer.writeInt32("year", value.getYear());
    writer.writeInt32("month", value.getMonthValue());
    writer.writeInt64("value", Integer.parseInt(String.format(
      "%d%02d",
      value.getYear(),
      value.getMonthValue()
    )));

    writer.writeEndDocument();
  }

  @Override
  public YearMonth decode(BsonReader reader, DecoderContext decoderContext) {
    requireNonNull(reader, "reader is null");

    reader.readStartDocument();

    int year = reader.readInt32("year");
    int month = reader.readInt32("month");
    reader.readInt64("value");

    reader.readEndDocument();

    return YearMonth.of(year, month);
  }

  @Override
  public Class<YearMonth> getEncoderClass() {
    return YearMonth.class;
  }

}
