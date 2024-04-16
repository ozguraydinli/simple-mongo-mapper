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

import java.time.Period;
import java.util.Map;

import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.time.Period.of;
import static java.util.Objects.requireNonNull;
import static tr.com.hive.smm.codecs.CodecsUtil.getFieldValue;
import static tr.com.hive.smm.codecs.CodecsUtil.readDocument;
import static tr.com.hive.smm.codecs.CodecsUtil.translateDecodeExceptions;

public final class PeriodAsDocumentCodec implements SMMCodec<Period> {

  private static final Map<String, Decoder<?>> FIELD_DECODERS = ImmutableMap.<String, Decoder<?>>builder()
                                                                            .put("years", (r, dc) -> r.readInt32())
                                                                            .put("months", (r, dc) -> r.readInt32())
                                                                            .put("days", (r, dc) -> r.readInt32())
                                                                            .put("yearsmonthsdays", (r, dc) -> r.readInt64())
                                                                            .put("iso8601", (r, dc) -> r.readString())
                                                                            .build();

  @Override
  public void encode(
    BsonWriter writer,
    Period value,
    EncoderContext encoderContext) {

    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");
    writer.writeStartDocument();
    writer.writeInt32("years", value.getYears());
    writer.writeInt32("months", value.getMonths());
    writer.writeInt32("days", value.getDays());
    writer.writeInt64("yearsmonthsdays", parseLong(format(
      "%d%02d%02d",
      value.getYears(),
      value.getMonths(),
      value.getDays()
    )));
    writer.writeString("iso8601", value.toString());
    writer.writeEndDocument();
  }

  @Override
  public Period decode(
    BsonReader reader,
    DecoderContext decoderContext) {

    requireNonNull(reader, "reader is null");
    return translateDecodeExceptions(
      () -> readDocument(reader, decoderContext, FIELD_DECODERS),
      val -> of(
        getFieldValue(val, "years", Integer.class),
        getFieldValue(val, "months", Integer.class),
        getFieldValue(val, "days", Integer.class)
      )
    );
  }

  @Override
  public Class<Period> getEncoderClass() {
    return Period.class;
  }

  @Override
  public Map<String, Decoder<?>> getFieldDecoders() {
    return FIELD_DECODERS;
  }

}
