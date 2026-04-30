package tr.com.hive.smm.codes.time;

import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonDocumentWriter;
import org.bson.BsonString;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.jsr310.LocalDateTimeCodec;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import tr.com.hive.smm.codecs.exception.InvalidZoneIdException;
import tr.com.hive.smm.codecs.time.ZoneIdAsStringCodec;
import tr.com.hive.smm.codecs.time.ZoneOffsetAsInt32Codec;
import tr.com.hive.smm.codecs.time.ZonedDateTimeAsDocumentCodec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ZonedDateTimeAsDocumentCodecTest {
  @Test
  void encode_ZonedDateTime_WritesComprehensiveDocument() {
    // Assuming you have a way to instantiate the codec with its dependencies
    ZonedDateTimeAsDocumentCodec codec = new ZonedDateTimeAsDocumentCodec(
      new LocalDateTimeCodec(), // Your implementation
      new ZoneOffsetAsInt32Codec(),
      new ZoneIdAsStringCodec()
    );

    BsonDocument document = new BsonDocument();
    BsonDocumentWriter writer = new BsonDocumentWriter(document);

    ZonedDateTime time = ZonedDateTime.of(
      2026, 4, 30, 8, 55, 58, 0, ZoneId.of("Europe/Berlin")
    );

    codec.encode(writer, time, EncoderContext.builder().build());

    // Assert the document structure
    assertEquals("Europe/Berlin", document.getString("zone").getValue());

    // Berlin is UTC+2 on April 30th (Daylight Saving Time), so 7200 seconds
    assertEquals(7200, document.getInt32("offset").getValue());

    assertTrue(document.containsKey("dateTime"));
    assertTrue(document.containsKey("value")); // Epoch millis

    assertEquals("2026-04-30T08:55:58+02:00[Europe/Berlin]", document.getString("valueString").getValue());
  }

}
