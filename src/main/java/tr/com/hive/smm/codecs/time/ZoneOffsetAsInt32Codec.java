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

import java.time.ZoneOffset;

import static java.util.Objects.requireNonNull;

public final class ZoneOffsetAsInt32Codec implements Codec<ZoneOffset> {

  @Override
  public void encode(BsonWriter writer, ZoneOffset value, EncoderContext encoderContext) {
    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");

    writer.writeInt32(value.getTotalSeconds());
  }

  @Override
  public ZoneOffset decode(BsonReader reader, DecoderContext decoderContext) {
    requireNonNull(reader, "reader is null");

    int totalSeconds = reader.readInt32();

    return ZoneOffset.ofTotalSeconds(totalSeconds);
  }

  @Override
  public Class<ZoneOffset> getEncoderClass() {
    return ZoneOffset.class;
  }

}
