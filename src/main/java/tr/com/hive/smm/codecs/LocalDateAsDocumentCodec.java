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

import java.time.LocalDate;
import java.util.Map;

import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static tr.com.hive.smm.codecs.CodecsUtil.getFieldValue;
import static tr.com.hive.smm.codecs.CodecsUtil.readDocument;
import static tr.com.hive.smm.codecs.CodecsUtil.translateDecodeExceptions;

public final class LocalDateAsDocumentCodec implements SMMCodec<LocalDate> {

  private static final Map<String, Decoder<?>> FIELD_DECODERS = ImmutableMap.<String, Decoder<?>>builder()
                                                                            .put("year", (r, dc) -> r.readInt32())
                                                                            .put("month", (r, dc) -> r.readInt32())
                                                                            .put("day", (r, dc) -> r.readInt32())
                                                                            .put("yearmonthday", (r, dc) -> r.readInt64())
                                                                            .build();

  @Override
  public void encode(
    BsonWriter writer,
    LocalDate value,
    EncoderContext encoderContext) {

    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");
    writer.writeStartDocument();
    writer.writeInt32("year", value.getYear());
    writer.writeInt32("month", value.getMonthValue());
    writer.writeInt32("day", value.getDayOfMonth());
    writer.writeInt64("yearmonthday", getYearMonthDay(value));
    writer.writeEndDocument();
  }

  @Override
  public LocalDate decode(
    BsonReader reader,
    DecoderContext decoderContext) {

    requireNonNull(reader, "reader is null");
    return translateDecodeExceptions(
      () -> readDocument(reader, decoderContext, FIELD_DECODERS),
      val -> LocalDate.of(
        getFieldValue(val, "year", Integer.class),
        getFieldValue(val, "month", Integer.class),
        getFieldValue(val, "day", Integer.class)
      )
    );
  }

  @Override
  public Class<LocalDate> getEncoderClass() {
    return LocalDate.class;
  }

  public long getYearMonthDay(LocalDate value) {
    return parseLong(format(
      "%d%02d%02d",
      value.getYear(),
      value.getMonthValue(),
      value.getDayOfMonth()
    ));
  }

  @Override
  public Map<String, Decoder<?>> getFieldDecoders() {
    return FIELD_DECODERS;
  }

}
