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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import static java.util.Objects.requireNonNull;


public final class OffsetTimeAsDocumentCodec implements Codec<OffsetTime> {

  private final Codec<LocalTime> localTimeCodec;
  private final Codec<ZoneOffset> zoneOffsetCodec;

  private OffsetTimeAsDocumentCodec(Codec<LocalTime> localTimeCodec, Codec<ZoneOffset> zoneOffsetCodec) {
    this.localTimeCodec = requireNonNull(localTimeCodec, "localTimeCodec is null");
    this.zoneOffsetCodec = requireNonNull(zoneOffsetCodec, "zoneOffsetCodec is null");
  }

  public OffsetTimeAsDocumentCodec(CodecRegistry codecRegistry) {
    this(codecRegistry.get(LocalTime.class), codecRegistry.get(ZoneOffset.class));
  }

  @Override
  public void encode(BsonWriter writer, OffsetTime value, EncoderContext encoderContext) {
    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");

    writer.writeStartDocument();

    writer.writeName("time");
    localTimeCodec.encode(writer, value.toLocalTime(), encoderContext);

    writer.writeName("offset");
    zoneOffsetCodec.encode(writer, value.getOffset(), encoderContext);

    writer.writeName("value");
    writer.writeDateTime(value.atDate(LocalDate.ofEpochDay(0L)).toInstant().toEpochMilli()); // This is the field that comparisons should be done

    writer.writeString("valueString", value.toString());

    writer.writeEndDocument();
  }

  @Override
  public OffsetTime decode(BsonReader reader, DecoderContext decoderContext) {
    requireNonNull(reader, "reader is null");

    reader.readStartDocument();

    reader.readName("time");
    localTimeCodec.decode(reader, decoderContext);
    zoneOffsetCodec.decode(reader, decoderContext);
    reader.readDateTime("value");

    String value = reader.readString("valueString");

    reader.readEndDocument();

    return OffsetTime.parse(value);
  }

  @Override
  public Class<OffsetTime> getEncoderClass() {
    return OffsetTime.class;
  }

}
