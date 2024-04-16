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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import static java.time.LocalDateTime.of;
import static java.util.Objects.requireNonNull;
import static org.bson.types.Decimal128.parse;
import static tr.com.hive.smm.codecs.CodecsUtil.getFieldValue;
import static tr.com.hive.smm.codecs.CodecsUtil.readDocument;
import static tr.com.hive.smm.codecs.CodecsUtil.translateDecodeExceptions;

public final class LocalDateTimeAsDocumentCodec
  implements Codec<LocalDateTime> {

  private final LocalDateAsDocumentCodec localDateCodec;
  private final LocalTimeAsDocumentCodec localTimeCodec;

  private final Map<String, Decoder<?>> fieldDecoders;

  /**
   * Creates a {@code LocalDateTimeAsDocumentCodec} using:
   * <ul>
   * <li>a {@link LocalDateAsDocumentCodec};
   * <li>a {@link LocalTimeAsDocumentCodec}.
   * </ul>
   */
  public LocalDateTimeAsDocumentCodec() {
    this(
      new LocalDateAsDocumentCodec(),
      new LocalTimeAsDocumentCodec()
    );
  }

  /**
   * Creates a {@code LocalDateTimeAsDocumentCodec} using
   * the provided codecs.
   *
   * @param localDateCodec not null
   * @param localTimeCodec not null
   */
  public LocalDateTimeAsDocumentCodec(
    LocalDateAsDocumentCodec localDateCodec,
    LocalTimeAsDocumentCodec localTimeCodec) {

    this.localDateCodec = requireNonNull(
      localDateCodec, "localDateCodec is null"
    );
    this.localTimeCodec = requireNonNull(
      localTimeCodec, "localTimeCodec is null"
    );

    fieldDecoders = ImmutableMap.<String, Decoder<?>>builder()
                                .put("date", localDateCodec)
                                .put("time", localTimeCodec)
                                .put("datetime", (r, dc) -> r.readDecimal128())
                                .build();
  }

  @Override
  public void encode(
    BsonWriter writer,
    LocalDateTime value,
    EncoderContext encoderContext) {

    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");

    writer.writeStartDocument();

    writer.writeName("date");
    localDateCodec.encode(writer, value.toLocalDate(), encoderContext);

    writer.writeName("time");
    localTimeCodec.encode(writer, value.toLocalTime(), encoderContext);

    writer.writeDecimal128(
      "datetime",
      parse(
        localDateCodec.getYearMonthDay(value.toLocalDate()) + "" + localTimeCodec.getHourMinuteSecondNano(value.toLocalTime())
      )
    );

    writer.writeEndDocument();
  }

  @Override
  public LocalDateTime decode(
    BsonReader reader,
    DecoderContext decoderContext) {

    requireNonNull(reader, "reader is null");
    return translateDecodeExceptions(
      () -> readDocument(reader, decoderContext, fieldDecoders),
      val -> of(
        getFieldValue(val, "date", LocalDate.class),
        getFieldValue(val, "time", LocalTime.class)
      )
    );
  }

  @Override
  public Class<LocalDateTime> getEncoderClass() {
    return LocalDateTime.class;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    LocalDateTimeAsDocumentCodec rhs = (LocalDateTimeAsDocumentCodec) obj;

    return localDateCodec.equals(rhs.localDateCodec) &&
           localTimeCodec.equals(rhs.localTimeCodec) &&
           fieldDecoders.equals(rhs.fieldDecoders);
  }

  @Override
  public int hashCode() {
    int result = localDateCodec.hashCode();
    result = 31 * result + localTimeCodec.hashCode();
    result = 31 * result + fieldDecoders.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "LocalDateTimeAsDocumentCodec[" +
           "localDateCodec=" + localDateCodec +
           ",localTimeCodec=" + localTimeCodec +
           ",fieldDecoders=" + fieldDecoders +
           ']';
  }

}
