/*
 * Copyright 2018 Cezary Bartosiak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tr.com.hive.smm.codecs;

import com.google.common.collect.ImmutableMap;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.Decoder;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

import static java.time.ZonedDateTime.ofStrict;
import static java.util.Objects.requireNonNull;
import static tr.com.hive.smm.codecs.CodecsUtil.getFieldValue;
import static tr.com.hive.smm.codecs.CodecsUtil.readDocument;
import static tr.com.hive.smm.codecs.CodecsUtil.translateDecodeExceptions;

/**
 * <p>
 * Encodes and decodes {@code ZonedDateTime} values to and from
 * {@code BSON Document}, such as:
 * <pre>
 * {
 *     dateTime: ...,
 *     offset: ...,
 *     zone: ...
 * }
 * </pre>
 * <p>
 * The values are stored using the following structure:
 * <ul>
 * <li>{@code dateTime} (a non-null value);
 * <li>{@code offset} (a non-null value);
 * <li>{@code zone} (a non-null value).
 * </ul>
 * The field values depend on provided codecs.
 * <p>
 * This type is <b>immutable</b>.
 */
public final class ZonedDateTimeAsDocumentCodec
  implements Codec<ZonedDateTime> {

  private final Codec<LocalDateTime> localDateTimeCodec;
  private final Codec<ZoneOffset> zoneOffsetCodec;
  private final Codec<ZoneId> zoneIdCodec;

  private final Map<String, Decoder<?>> fieldDecoders;

  /**
   * Creates a {@code ZonedDateTimeAsDocumentCodec} using:
   * <ul>
   * <li>a {@link LocalDateTimeAsDocumentCodec};
   * <li>a {@link ZoneOffsetAsInt32Codec};
   * <li>a {@link ZoneIdAsStringCodec}.
   * </ul>
   */
  public ZonedDateTimeAsDocumentCodec() {
    this(
      new LocalDateTimeAsDocumentCodec(),
      new ZoneOffsetAsInt32Codec(),
      new ZoneIdAsStringCodec()
    );
  }

  /**
   * Creates a {@code ZonedDateTimeAsDocumentCodec} using
   * the provided codecs.
   *
   * @param localDateTimeCodec not null
   * @param zoneOffsetCodec    not null
   * @param zoneIdCodec        not null
   */
  public ZonedDateTimeAsDocumentCodec(
    Codec<LocalDateTime> localDateTimeCodec,
    Codec<ZoneOffset> zoneOffsetCodec,
    Codec<ZoneId> zoneIdCodec) {

    this.localDateTimeCodec = requireNonNull(
      localDateTimeCodec, "localDateTimeCodec is null"
    );
    this.zoneOffsetCodec = requireNonNull(
      zoneOffsetCodec, "zoneOffsetCodec is null"
    );
    this.zoneIdCodec = requireNonNull(
      zoneIdCodec, "zoneIdCodec is null"
    );

    fieldDecoders = ImmutableMap.<String, Decoder<?>>builder()
                                .put("dateTime", localDateTimeCodec)
                                .put("offset", zoneOffsetCodec)
                                .put("zone", zoneIdCodec)
                                .build();
  }

  @Override
  public void encode(
    BsonWriter writer,
    ZonedDateTime value,
    EncoderContext encoderContext) {

    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");

    writer.writeStartDocument();

    writer.writeName("dateTime");
    localDateTimeCodec.encode(
      writer, value.toLocalDateTime(), encoderContext
    );

    writer.writeName("offset");
    zoneOffsetCodec.encode(writer, value.getOffset(), encoderContext);

    writer.writeName("zone");
    zoneIdCodec.encode(writer, value.getZone(), encoderContext);

    writer.writeEndDocument();
  }

  @Override
  public ZonedDateTime decode(
    BsonReader reader,
    DecoderContext decoderContext) {

    requireNonNull(reader, "reader is null");
    return translateDecodeExceptions(
      () -> readDocument(reader, decoderContext, fieldDecoders),
      val -> ofStrict(
        getFieldValue(val, "dateTime", LocalDateTime.class),
        getFieldValue(val, "offset", ZoneOffset.class),
        getFieldValue(val, "zone", ZoneId.class)
      )
    );
  }

  @Override
  public Class<ZonedDateTime> getEncoderClass() {
    return ZonedDateTime.class;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    ZonedDateTimeAsDocumentCodec rhs = (ZonedDateTimeAsDocumentCodec) obj;

    return localDateTimeCodec.equals(rhs.localDateTimeCodec) &&
           zoneOffsetCodec.equals(rhs.zoneOffsetCodec) &&
           zoneIdCodec.equals(rhs.zoneIdCodec) &&
           fieldDecoders.equals(rhs.fieldDecoders);
  }

  @Override
  public int hashCode() {
    int result = localDateTimeCodec.hashCode();
    result = 31 * result + zoneOffsetCodec.hashCode();
    result = 31 * result + zoneIdCodec.hashCode();
    result = 31 * result + fieldDecoders.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "ZonedDateTimeAsDocumentCodec[" +
           "localDateTimeCodec=" + localDateTimeCodec +
           ",zoneOffsetCodec=" + zoneOffsetCodec +
           ",zoneIdCodec=" + zoneIdCodec +
           ",fieldDecoders=" + fieldDecoders +
           ']';
  }

}
