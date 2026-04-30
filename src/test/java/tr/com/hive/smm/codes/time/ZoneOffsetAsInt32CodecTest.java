package tr.com.hive.smm.codes.time;

import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonDocumentWriter;
import org.bson.BsonInt32;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;

import tr.com.hive.smm.codecs.time.ZoneOffsetAsInt32Codec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ZoneOffsetAsInt32CodecTest {

  @Test
  void encode_ZoneOffset_WritesTotalSecondsAsInt32() {
    ZoneOffsetAsInt32Codec codec = new ZoneOffsetAsInt32Codec();
    BsonDocument document = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(document);

    // +01:00 is 3600 seconds
    ZoneOffset offset = ZoneOffset.ofHours(1);

    writer.writeStartDocument();
    writer.writeName("offset");
    codec.encode(writer, offset, EncoderContext.builder().build());
    writer.writeEndDocument();

    assertTrue(document.containsKey("offset"));
    assertTrue(document.get("offset").isInt32());
    assertEquals(3600, document.getInt32("offset").getValue());
  }

  @Test
  void decode_Int32Bson_ReturnsZoneOffset() {
    ZoneOffsetAsInt32Codec codec = new ZoneOffsetAsInt32Codec();

    // -05:00 is -18000 seconds
    BsonDocument doc = new BsonDocument("offset", new BsonInt32(-18000));
    BsonDocumentReader reader = new BsonDocumentReader(doc);

    reader.readStartDocument();
    reader.readName("offset");

    ZoneOffset result = codec.decode(reader, DecoderContext.builder().build());
    reader.readEndDocument();

    assertEquals(ZoneOffset.ofHours(-5), result);
  }

}
