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

package tr.com.hive.smm.codecs.time;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static java.util.Objects.requireNonNull;

/**
 * <p>
 * Encodes and decodes {@code OffsetDateTime} values to and from
 * {@code BSON Document}, such as:
 * <pre>
 * {
 *     dateTime: ...,
 *     offset: ...
 * }
 * </pre>
 * <p>
 * The values are stored using the following structure:
 * <ul>
 * <li>{@code dateTime} (a non-null value);
 * <li>{@code offset} (a non-null value).
 * </ul>
 * The field values depend on provided codecs.
 * <p>
 * This type is <b>immutable</b>.
 */
public final class OffsetDateTimeAsDocumentCodec implements Codec<OffsetDateTime> {

  private final Codec<ZoneOffset> zoneOffsetCodec;


  /**
   * Creates an {@code OffsetDateTimeAsDocumentCodec} using
   * the provided codecs.
   *
   * @param zoneOffsetCodec not null
   */
  private OffsetDateTimeAsDocumentCodec(Codec<ZoneOffset> zoneOffsetCodec) {
    this.zoneOffsetCodec = requireNonNull(zoneOffsetCodec, "zoneOffsetCodec is null");
  }

  public OffsetDateTimeAsDocumentCodec(CodecRegistry codecRegistry) {
    this(codecRegistry.get(ZoneOffset.class));
  }

  @Override
  public void encode(BsonWriter writer, OffsetDateTime value, EncoderContext encoderContext) {
    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");

    writer.writeStartDocument();

    writer.writeName("dateTime");
    writer.writeDateTime(value.toInstant().toEpochMilli());

    writer.writeName("offset");
    zoneOffsetCodec.encode(writer, value.getOffset(), encoderContext);

    writer.writeString("value", value.toString());

    writer.writeEndDocument();
  }

  @Override
  public OffsetDateTime decode(BsonReader reader, DecoderContext decoderContext) {
    requireNonNull(reader, "reader is null");

    reader.readStartDocument();

    reader.readDateTime();
    zoneOffsetCodec.decode(reader, decoderContext);

    String value = reader.readString("value");

    reader.readEndDocument();

    return OffsetDateTime.parse(value);
  }

  @Override
  public Class<OffsetDateTime> getEncoderClass() {
    return OffsetDateTime.class;
  }

}
