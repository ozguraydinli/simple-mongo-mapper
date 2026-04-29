package tr.com.hive.smm.codes.time;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import tr.com.hive.smm.codecs.time.ZoneIdAsStringCodec;

public class ZoneIdAsStringCodecTest {
  @Test
  void test() {
    ZoneId zoneId = ZoneId.of("Europe/Istanbul");
    ZoneIdAsStringCodec zoneIdAsStringCodec = new ZoneIdAsStringCodec();
    BsonDocumentWriter writer = new BsonDocumentWriter(new BsonDocument());
    writer.writeStartDocument();
    writer.writeName("zoneId");
    zoneIdAsStringCodec.encode(writer, zoneId, null);
//    BsonDocument zoneIdValue = new BsonDocument("zoneId", new BsonString("Europe/Istanbul"));
//    BsonDocumentReader reader = new BsonDocumentReader(zoneIdValue);
//    reader.readBsonType("zoneId");
//    zoneIdAsStringCodec.decode(reader, null);
  }
}
