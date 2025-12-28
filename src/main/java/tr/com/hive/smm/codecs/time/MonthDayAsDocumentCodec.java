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

import java.time.MonthDay;

import static java.util.Objects.requireNonNull;

public final class MonthDayAsDocumentCodec implements Codec<MonthDay> {

  @Override
  public void encode(BsonWriter writer, MonthDay value, EncoderContext encoderContext) {
    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");

    writer.writeStartDocument();
    writer.writeInt32("month", value.getMonthValue());
    writer.writeInt32("day", value.getDayOfMonth());
    writer.writeInt64("value", Integer.parseInt(String.format(
      "%d%02d",
      value.getMonthValue(),
      value.getDayOfMonth()
    )));

    writer.writeEndDocument();
  }

  @Override
  public MonthDay decode(BsonReader reader, DecoderContext decoderContext) {
    requireNonNull(reader, "reader is null");

    reader.readStartDocument();

    int month = reader.readInt32("month");
    int day = reader.readInt32("day");
    reader.readInt64("value");

    reader.readEndDocument();

    return MonthDay.of(month, day);
  }

  @Override
  public Class<MonthDay> getEncoderClass() {
    return MonthDay.class;
  }

}
