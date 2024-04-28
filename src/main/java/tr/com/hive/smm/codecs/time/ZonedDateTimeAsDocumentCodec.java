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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static java.util.Objects.requireNonNull;


public final class ZonedDateTimeAsDocumentCodec implements Codec<ZonedDateTime> {

  private final Codec<LocalDateTime> localDateTimeCodec;
  private final Codec<ZoneOffset> zoneOffsetCodec;
  private final Codec<ZoneId> zoneIdCodec;

  private ZonedDateTimeAsDocumentCodec(Codec<LocalDateTime> localDateTimeCodec, Codec<ZoneOffset> zoneOffsetCodec, Codec<ZoneId> zoneIdCodec) {
    this.localDateTimeCodec = requireNonNull(localDateTimeCodec, "localDateTimeCodec is null");
    this.zoneOffsetCodec = requireNonNull(zoneOffsetCodec, "zoneOffsetCodec is null");
    this.zoneIdCodec = requireNonNull(zoneIdCodec, "zoneIdCodec is null");
  }

  public ZonedDateTimeAsDocumentCodec(CodecRegistry codecRegistry) {
    this(codecRegistry.get(LocalDateTime.class), codecRegistry.get(ZoneOffset.class), codecRegistry.get(ZoneId.class));
  }

  @Override
  public void encode(BsonWriter writer, ZonedDateTime value, EncoderContext encoderContext) {
    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");

    writer.writeStartDocument();

    writer.writeName("dateTime");
    localDateTimeCodec.encode(writer, value.toLocalDateTime(), encoderContext);

    writer.writeName("offset");
    zoneOffsetCodec.encode(writer, value.getOffset(), encoderContext);

    writer.writeName("zone");
    zoneIdCodec.encode(writer, value.getZone(), encoderContext);

    writer.writeName("valueInstant");
    writer.writeDateTime(value.toInstant().toEpochMilli()); // This is the field that comparisons should be done

    writer.writeString("value", value.toString());

    writer.writeEndDocument();
  }

  @Override
  public ZonedDateTime decode(BsonReader reader, DecoderContext decoderContext) {
    requireNonNull(reader, "reader is null");

    reader.readStartDocument();

    reader.readName("dateTime");
    localDateTimeCodec.decode(reader, decoderContext);
    zoneOffsetCodec.decode(reader, decoderContext);
    zoneIdCodec.decode(reader, decoderContext);
    reader.readDateTime("valueInstant");

    String value = reader.readString("value");

    reader.readEndDocument();

    return ZonedDateTime.parse(value);
  }

  @Override
  public Class<ZonedDateTime> getEncoderClass() {
    return ZonedDateTime.class;
  }

}
