package tr.com.hive.smm.codecs;

import com.google.common.collect.ImmutableMap;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.Decoder;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.Month;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static tr.com.hive.smm.codecs.CodecsUtil.getFieldValue;
import static tr.com.hive.smm.codecs.CodecsUtil.readDocument;
import static tr.com.hive.smm.codecs.CodecsUtil.translateDecodeExceptions;

public class MonthAsDocumentCodec implements Codec<Month> {

  private static final Map<String, Decoder<?>> FIELD_DECODERS = ImmutableMap.<String, Decoder<?>>builder()
                                                                            .put("monthstring", (r, dc) -> r.readString())
                                                                            .put("monthinteger", (r, dc) -> r.readInt32())
                                                                            .build();

  @Override
  public void encode(
    BsonWriter writer,
    Month value,
    EncoderContext encoderContext) {

    requireNonNull(writer, "writer is null");
    requireNonNull(value, "value is null");

    writer.writeStartDocument();
    writer.writeString(value.name());
    writer.writeInt32(value.getValue());
    writer.writeEndDocument();
  }

  @Override
  public Month decode(
    BsonReader reader,
    DecoderContext decoderContext) {

    requireNonNull(reader, "reader is null");
    return translateDecodeExceptions(
      () -> readDocument(reader, decoderContext, FIELD_DECODERS),
      val -> Month.of(getFieldValue(val, "monthinteger", Integer.class))
    );
  }

  @Override
  public Class<Month> getEncoderClass() {
    return Month.class;
  }



}
