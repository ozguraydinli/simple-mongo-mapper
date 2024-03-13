package tr.com.hive.smm.mapping2;

import com.google.common.collect.Maps;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MyCodec<T extends Map<String, List<Map<String, String>>>> extends DefaultCodec<T> implements Codec<T> {

  public MyCodec(Class<?> aClass) {
    super(aClass);
  }

  @SuppressWarnings("unchecked")
  @Override
  public T decode(BsonReader reader, DecoderContext decoderContext) {

    Map<String, List<Map<String, String>>> tableValues = Maps.newHashMap();

    reader.readStartDocument();

    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      String fieldName = reader.readName();
      tableValues.put(fieldName, new ArrayList<>());

      reader.readStartArray();

      while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {

        reader.readStartDocument();

        Map<String, String> innerMap = Maps.newHashMap();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
          innerMap.put(reader.readName(), reader.readString());
        }
        tableValues.get(fieldName).add(innerMap);

        reader.readEndDocument();
      }

      reader.readEndArray();
    }

    reader.readEndDocument();

    return (T) tableValues;
  }

  @Override
  public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
    writer.writeStartDocument();

    Set<Entry<String, List<Map<String, String>>>> entries = value.entrySet();
    for (Entry<String, List<Map<String, String>>> entry : entries) {
      writer.writeStartArray(entry.getKey());

      List<Map<String, String>> list = entry.getValue();
      for (Map<String, String> innerMap : list) {
        writer.writeStartDocument();

        innerMap.forEach((k, v) -> writer.writeString(k + "123", v));

        writer.writeEndDocument();
      }

      writer.writeEndArray();
    }

    writer.writeEndDocument();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<T> getEncoderClass() {
    return (Class<T>) aClass;
  }

}
