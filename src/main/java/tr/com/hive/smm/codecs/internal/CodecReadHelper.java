/*
 * Copyright (c)  Hive A.S. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by ozgur, 2026
 */

package tr.com.hive.smm.codecs.internal;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CodecReadHelper {

  private final CodecRegistry registry;
  private final BsonReader reader;
  private final DecoderContext decoderContext;

  public void readDocument(Map<String, Runnable> fieldHandlers) {
    reader.readStartDocument();

    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      String fieldName = reader.readName();
      Runnable handler = fieldHandlers.get(fieldName);
      if (handler != null) {
        handler.run();
      } else {
        reader.skipValue();
      }
    }

    reader.readEndDocument();
  }

  public String readString() {
    return reader.readString();
  }

  public ObjectId readObjectId() {
    return reader.readObjectId();
  }

  public int readInt32() {
    return reader.readInt32();
  }

  public long readInt64() {
    return reader.readInt64();
  }

  public boolean readBoolean() {
    return reader.readBoolean();
  }

  public <T extends Enum<T>> T readEnum(Class<T> clazz) {
    try {
      return Enum.valueOf(clazz, reader.readString());
    } catch (IllegalArgumentException | BsonInvalidOperationException e) {
      throw new IllegalStateException("");
    }
  }

  public <T> T read(Class<T> clazz) {
    Codec<T> codec = registry.get(clazz);
    return decoderContext.decodeWithChildContext(codec, reader);
  }

  public <T> List<T> readList(Class<T> clazz) {
    List<T> list = new ArrayList<>();
    reader.readStartArray();
    Codec<T> codec = registry.get(clazz);
    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      list.add(decoderContext.decodeWithChildContext(codec, reader));
    }
    reader.readEndArray();
    return list;
  }

}
