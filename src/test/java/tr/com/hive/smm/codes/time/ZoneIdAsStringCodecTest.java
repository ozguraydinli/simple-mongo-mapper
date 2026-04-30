package tr.com.hive.smm.codes.time;

import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonDocumentWriter;
import org.bson.BsonString;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;

import tr.com.hive.smm.codecs.exception.InvalidZoneIdException;
import tr.com.hive.smm.codecs.time.ZoneIdAsStringCodec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ZoneIdAsStringCodecTest {

  @Test
  void encode_RegionBasedZoneId_WritesNormalizedString() {

    ZoneIdAsStringCodec zoneIdAsStringCodec = new ZoneIdAsStringCodec();

    BsonDocument document = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(document);

    ZoneId zoneId = ZoneId.of("Europe/Berlin");

    writer.writeStartDocument();
    writer.writeName("zone");
    zoneIdAsStringCodec.encode(writer, zoneId, EncoderContext.builder().build());
    writer.writeEndDocument();

    assertTrue(document.containsKey("zone"));
    assertEquals("Europe/Berlin", document.getString("zone").getValue());
  }

  @Test
  void encode_OffsetBasedZoneId_WritesIdString() {
    ZoneIdAsStringCodec zoneIdAsStringCodec = new ZoneIdAsStringCodec();

    BsonDocument document = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(document);

    ZoneId zoneOffset = ZoneOffset.of("+05:30");

    writer.writeStartDocument();
    writer.writeName("offset");
    zoneIdAsStringCodec.encode(writer, zoneOffset, EncoderContext.builder().build());
    writer.writeEndDocument();

    assertEquals("+05:30", document.getString("offset").getValue());
  }

  @Test
  void decode_RealBsonString_ReturnsZoneId() {
    ZoneIdAsStringCodec zoneIdAsStringCodec = new ZoneIdAsStringCodec();

    BsonDocument doc = new BsonDocument("id", new BsonString("Europe/London"));
    BsonDocumentReader reader = new BsonDocumentReader(doc);

    reader.readStartDocument();
    reader.readName("id");

    ZoneId result = zoneIdAsStringCodec.decode(reader, DecoderContext.builder().build());
    reader.readEndDocument();

    assertEquals(ZoneId.of("Europe/London"), result);
  }

  @Test
  void decode_RealOffsetString_ReturnsZoneOffset() {
    ZoneIdAsStringCodec zoneIdAsStringCodec = new ZoneIdAsStringCodec();
    BsonDocument doc = new BsonDocument("id", new BsonString("-05:00"));
    BsonDocumentReader reader = new BsonDocumentReader(doc);
    reader.readStartDocument();
    reader.readName("id");
    ZoneId result = zoneIdAsStringCodec.decode(reader, DecoderContext.builder().build());
    reader.readEndDocument();
    assertEquals(ZoneOffset.of("-05:00"), result);
  }

  @Test
  void encode_PrefixOffset_WritesNormalizedId() {
    ZoneIdAsStringCodec codec = new ZoneIdAsStringCodec();
    BsonDocument document = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(document);

    // ZoneId.of("UTC+05:00") is a ZoneRegion, but normalized() makes it a ZoneOffset
    ZoneId zoneId = ZoneId.of("UTC+05:00");

    writer.writeStartDocument();
    writer.writeName("zone");
    codec.encode(writer, zoneId, EncoderContext.builder().build());
    writer.writeEndDocument();

    assertEquals("+05:00", document.getString("zone").getValue());
  }

  @Test
  void decode_InvalidZoneString_ThrowsDateTimeException() {
    ZoneIdAsStringCodec codec = new ZoneIdAsStringCodec();
    BsonDocument doc = new BsonDocument("id", new BsonString("Invalid/Zone_Name"));
    BsonDocumentReader reader = new BsonDocumentReader(doc);

    reader.readStartDocument();
    reader.readName();

    InvalidZoneIdException exception = assertThrows(InvalidZoneIdException.class, () -> {
      codec.decode(reader, DecoderContext.builder().build());
    });

    assertTrue(exception.getMessage().contains("Invalid/Zone_Name"));
  }

}
