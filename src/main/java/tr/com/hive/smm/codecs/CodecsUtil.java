/*
 * Copyright (c)  Hive A.S. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by ismet, 2024
 */

package tr.com.hive.smm.codecs;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.Document;
import org.bson.codecs.Decoder;
import org.bson.codecs.DecoderContext;

import java.time.DateTimeException;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.bson.BsonType.END_OF_DOCUMENT;

public final class CodecsUtil {

  private CodecsUtil() {
  }

  // Exceptions
  public static <Value, Result> Result translateDecodeExceptions(
    Supplier<Value> valueSupplier,
    Function<Value, Result> valueConverter) {

    Value value = valueSupplier.get();
    try {
      return valueConverter.apply(value);
    } catch (ArithmeticException |
             DateTimeException |
             IllegalArgumentException ex) {

      throw new BsonInvalidOperationException(format(
        "The value %s is not supported", value
      ), ex);
    }
  }

  // Document codecs

  public static Document readDocument(
    BsonReader reader,
    DecoderContext decoderContext,
    Map<String, Decoder<?>> fieldDecoders) {

    Document document = new Document();
    reader.readStartDocument();
    while (reader.readBsonType() != END_OF_DOCUMENT) {
      String fieldName = reader.readName();
      if (fieldDecoders.containsKey(fieldName)) {
        document.put(
          fieldName,
          fieldDecoders
            .get(fieldName)
            .decode(reader, decoderContext)
        );
      } else {
        throw new BsonInvalidOperationException(format(
          "The field %s is not expected here", fieldName
        ));
      }
    }
    reader.readEndDocument();
    return document;
  }

  public static <Value> Value getFieldValue(
    Document document,
    Object key,
    Class<Value> clazz) {

    try {
      Value value = document.get(key, clazz);
      if (value == null) {
        throw new BsonInvalidOperationException(format(
          "The value of the field %s is null", key
        ));
      }
      return value;
    } catch (ClassCastException ex) {
      throw new BsonInvalidOperationException(format(
        "The value of the field %s is not of the type %s",
        key, clazz.getName()
      ), ex);
    }
  }

}
