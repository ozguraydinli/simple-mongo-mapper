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

import java.time.Duration;
import java.util.Map;

import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static java.util.Objects.requireNonNull;
import static org.bson.types.Decimal128.parse;
import static tr.com.hive.smm.codecs.CodecsUtil.getFieldValue;
import static tr.com.hive.smm.codecs.CodecsUtil.readDocument;
import static tr.com.hive.smm.codecs.CodecsUtil.translateDecodeExceptions;

public class DurationAsDocumentCodec implements Codec<Duration> {

  private static final Map<String, Decoder<?>> FIELD_DECODERS = ImmutableMap.<String, Decoder<?>>builder()
                                                                            .put("seconds", (r, dc) -> r.readInt64())
                                                                            .put("nanos", (r, dc) -> r.readInt32())
                                                                            .put("secondsnanos", (r, dc) -> r.readDecimal128())
                                                                            .build();

  @Override
  public void encode(
    BsonWriter writer,
    Duration value,
    EncoderContext encoderContext) {

    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");
    writer.writeStartDocument();
    writer.writeInt64("seconds", value.getSeconds());
    writer.writeInt32("nanos", value.getNano());
    writer.writeDecimal128("secondsnanos", parse(format(
      "%d%010d",
      value.getSeconds(),
      value.getNano()
    )));
    writer.writeEndDocument();
  }

  @Override
  public Duration decode(
    BsonReader reader,
    DecoderContext decoderContext) {

    requireNonNull(reader, "reader is null");
    return translateDecodeExceptions(
      () -> readDocument(reader, decoderContext, FIELD_DECODERS),
      val -> ofSeconds(
        getFieldValue(val, "seconds", Long.class),
        getFieldValue(val, "nanos", Integer.class)
      )
    );
  }

  @Override
  public Class<Duration> getEncoderClass() {
    return Duration.class;
  }

}
