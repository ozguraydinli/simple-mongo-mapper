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

import java.time.Year;

import static java.util.Objects.requireNonNull;


public final class YearAsInt32Codec implements Codec<Year> {

  @Override
  public void encode(BsonWriter writer, Year value, EncoderContext encoderContext) {
    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");

    writer.writeInt32(value.getValue());
  }

  @Override
  public Year decode(BsonReader reader, DecoderContext decoderContext) {
    requireNonNull(reader, "reader is null");

    int year = reader.readInt32();

    return Year.of(year);
  }

  @Override
  public Class<Year> getEncoderClass() {
    return Year.class;
  }

}
