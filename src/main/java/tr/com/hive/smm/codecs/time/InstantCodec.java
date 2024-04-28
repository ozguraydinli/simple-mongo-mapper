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

import java.time.Instant;

/**
 * High precision Instant codec.
 */
public class InstantCodec implements Codec<Instant> {

  @Override
  public void encode(final BsonWriter writer, final Instant value, final EncoderContext encoderContext) {
    writer.writeStartDocument();

    writer.writeDateTime("date", value.toEpochMilli());
    writer.writeString("value", value.toString());

    writer.writeEndDocument();
  }

  @Override
  public Instant decode(final BsonReader reader, final DecoderContext decoderContext) {
    reader.readStartDocument();

    reader.readDateTime("date");
    String value = reader.readString("value");

    reader.readEndDocument();

    return Instant.parse(value);
  }

  @Override
  public Class<Instant> getEncoderClass() {
    return Instant.class;
  }

}
