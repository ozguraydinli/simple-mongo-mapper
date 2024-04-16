/*
 * Copyright 2018 Cezary Bartosiak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tr.com.hive.smm.codecs;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static java.lang.Long.MAX_VALUE;
import static java.time.Duration.ZERO;
import static java.time.Duration.ofHours;
import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class CodecsTests extends AbstractCodecsTests {

    private CodecsTests() {}

    @Test
    void testDurationAsDocumentCodec() {
        DurationAsDocumentCodec codec = new DurationAsDocumentCodec();
        assertThrows(
          NullPointerException.class,
          () -> testCodec(codec, null)
        );
        testCodec(codec, ZERO);
        testCodec(codec, ofSeconds(MAX_VALUE, 999_999_999L));
        testCodec(codec, ofHours(12));

        Duration dur1 = Duration.ofDays(455);
        Document doc1 = encodeDecodeAndGetDocument(dur1, codec);

        assertEquals(doc1.getLong("seconds"), 39312000L);
        assertEquals(doc1.getInteger("nanos"), 0);
        assertEquals(doc1.get("secondsnanos", Decimal128.class), Decimal128.parse("39312000.000000000"));

        Duration dur2 = Duration.ofSeconds(100, 99L);
        Document doc2 = encodeDecodeAndGetDocument(dur2, codec);
        assertEquals(doc2.getLong("seconds"), 100L);
        assertEquals(doc2.getInteger("nanos"), 99);
        assertEquals(doc2.get("secondsnanos", Decimal128.class), Decimal128.parse("100.000000099"));

    }
}
