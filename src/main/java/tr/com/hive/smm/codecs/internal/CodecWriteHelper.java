/*
 * Copyright (c)  Hive A.S. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by ozgur, 2026
 */

package tr.com.hive.smm.codecs.internal;

import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CodecWriteHelper {

  private final CodecRegistry registry;
  private final BsonWriter writer;
  private final EncoderContext encoderContext;

  public void writeString(String fieldName, Supplier<String> block) {
    String str = block.get();
    if (str != null) {
      writer.writeString(fieldName, str);
    }
  }

  public void writeEnum(String fieldName, Supplier<Enum<?>> block) {
    Enum<?> anEnum = block.get();
    if (anEnum != null) {
      writer.writeString(fieldName, anEnum.name());
    }
  }

  public void writeObjectId(String fieldName, Supplier<ObjectId> block) {
    ObjectId objectId = block.get();
    if (objectId != null) {
      writer.writeObjectId(fieldName, objectId);
    }
  }

  public void writeInt32(String fieldName, Supplier<Integer> block) {
    Integer i = block.get();
    if (i != null) {
      writer.writeInt32(fieldName, i);
    }
  }

  public void writeInt64(String fieldName, Supplier<Long> block) {
    Long i = block.get();
    if (i != null) {
      writer.writeInt64(fieldName, i);
    }
  }

  public void writeBoolean(String fieldName, Supplier<Boolean> block) {
    Boolean bool = block.get();
    if (bool != null) {
      writer.writeBoolean(fieldName, bool);
    }
  }

  public <T> void write(String fieldName, Class<T> clazz, Supplier<T> block) {
    T t = block.get();
    if (t != null) {
      writer.writeName(fieldName);
      Codec<T> codec = registry.get(clazz);
      encoderContext.encodeWithChildContext(codec, writer, t);
    }
  }

  public <T> void writeCollection(String fieldName, Class<T> clazz, Supplier<? extends Collection<T>> supplier) {
    Collection<T> collection = supplier.get();
    if (collection != null && !collection.isEmpty()) {
      writer.writeName(fieldName);
      writer.writeStartArray();
      Codec<T> codec = registry.get(clazz);
      for (T item : collection) {
        encoderContext.encodeWithChildContext(codec, writer, item);
      }
      writer.writeEndArray();
    }
  }

  public void document(Runnable block) {
    writer.writeStartDocument();
    block.run();
    writer.writeEndDocument();
  }

}
