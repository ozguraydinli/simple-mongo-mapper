package tr.com.hive.smm.mapping2;

import com.google.common.collect.Maps;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import tr.com.hive.smm.codecs.internal.DefaultCodec;
import tr.com.hive.smm.model.MyEnum;

/*
*
* Workaround for enum to enum map
* For some reason default codec cannot make an update query, get and insert is OK
*
* */
public class MyEnumCodec<T extends Map<MyEnum, MyEnum>> extends DefaultCodec<T> implements Codec<T> {

  public MyEnumCodec(Class<?> aClass) {
    super(aClass);
  }

  @SuppressWarnings("unchecked")
  @Override
  public T decode(BsonReader reader, DecoderContext decoderContext) {

    Map<MyEnum, MyEnum> tableValues = Maps.newHashMap();

    reader.readStartDocument();

    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      String fieldName = reader.readName();
      tableValues.put(MyEnum.valueOf(fieldName), MyEnum.valueOf(reader.readString()));
    }

    reader.readEndDocument();

    return (T) tableValues;
  }

  @Override
  public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
    writer.writeStartDocument();

    Set<Entry<MyEnum, MyEnum>> entries = value.entrySet();
    for (Entry<MyEnum, MyEnum> entry : entries) {
      writer.writeString(entry.getKey().name(), entry.getValue().name());
    }

    writer.writeEndDocument();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Class<T> getEncoderClass() {
    return (Class<T>) aClass;
  }

}
