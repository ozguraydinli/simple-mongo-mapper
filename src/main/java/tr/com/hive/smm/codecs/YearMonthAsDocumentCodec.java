/*
 * Copyright (c)  Hive A.S. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by ismet, 2024
 */

package tr.com.hive.smm.codecs;

import com.google.common.collect.ImmutableMap;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.Decoder;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.YearMonth;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.time.YearMonth.of;
import static java.util.Objects.requireNonNull;
import static tr.com.hive.smm.codecs.CodecsUtil.getFieldValue;
import static tr.com.hive.smm.codecs.CodecsUtil.readDocument;
import static tr.com.hive.smm.codecs.CodecsUtil.translateDecodeExceptions;

public final class YearMonthAsDocumentCodec implements Codec<YearMonth> {

  private static final Map<String, Decoder<?>> FIELD_DECODERS = ImmutableMap.<String, Decoder<?>>builder()
                                                                            .put("year", (r, dc) -> r.readInt64())
                                                                            .put("month", (r, dc) -> r.readInt32())
                                                                            .put("yearmonth", (r, dc) -> r.readInt32())
                                                                            .build();
  ;

  @Override
  public void encode(
    BsonWriter writer,
    YearMonth value,
    EncoderContext encoderContext) {

    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");
    writer.writeStartDocument();
    writer.writeInt32("year", value.getYear());
    writer.writeInt32("month", value.getMonthValue());
    writer.writeInt64("yearmonth", parseInt(format(
      "%d%02d",
      value.getYear(),
      value.getMonthValue()
    )));
    writer.writeEndDocument();
  }

  @Override
  public YearMonth decode(
    BsonReader reader,
    DecoderContext decoderContext) {

    requireNonNull(reader, "reader is null");
    return translateDecodeExceptions(
      () -> readDocument(reader, decoderContext, FIELD_DECODERS),
      val -> of(
        getFieldValue(val, "year", Integer.class),
        getFieldValue(val, "month", Integer.class)
      )
    );
  }

  @Override
  public Class<YearMonth> getEncoderClass() {
    return YearMonth.class;
  }

}
