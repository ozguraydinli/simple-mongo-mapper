/*
 * Copyright (c)  Hive A.S. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by ismet, 2024
 */

package tr.com.hive.smm.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.ZoneId;

import static java.util.Objects.requireNonNull;
import static tr.com.hive.smm.codecs.CodecsUtil.translateDecodeExceptions;

/**
 * <p>
 * Encodes and decodes {@code ZoneId} values to and from
 * {@code BSON String}, such as
 * {@code CET} (non-offset based IDs) or
 * {@code +01:00} (offset based IDs).
 * <p>
 * The values are stored as IDs
 * (see {@link ZoneId#getId()}).
 * <p>
 * This type is <b>immutable</b>.
 */
public final class ZoneIdAsStringCodec implements Codec<ZoneId> {

  @Override
  public void encode(
    BsonWriter writer,
    ZoneId value,
    EncoderContext encoderContext) {

    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");
    writer.writeString(value.getId());
  }

  @Override
  public ZoneId decode(
    BsonReader reader,
    DecoderContext decoderContext) {

    requireNonNull(reader, "reader is null");
    return translateDecodeExceptions(
      reader::readString,
      ZoneId::of
    );
  }

  @Override
  public Class<ZoneId> getEncoderClass() {
    return ZoneId.class;
  }

}
