/*
 * Copyright (c)  Hive A.S. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by ismet, 2024
 */

package tr.com.hive.smm.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.Year;

import static java.util.Objects.requireNonNull;
import static tr.com.hive.smm.codecs.CodecsUtil.translateDecodeExceptions;


public final class YearAsInt32Codec implements Codec<Year> {

  @Override
  public void encode(
    BsonWriter writer,
    Year value,
    EncoderContext encoderContext) {

    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");
    writer.writeInt32(value.getValue());
  }

  @Override
  public Year decode(
    BsonReader reader,
    DecoderContext decoderContext) {

    requireNonNull(reader, "reader is null");
    return translateDecodeExceptions(
      reader::readInt32,
      Year::of
    );
  }

  @Override
  public Class<Year> getEncoderClass() {
    return Year.class;
  }

}
