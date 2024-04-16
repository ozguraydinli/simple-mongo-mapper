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

import org.bson.io.BasicOutputBuffer;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static java.lang.Long.MAX_VALUE;
import static java.nio.ByteBuffer.wrap;
import static java.time.Duration.ZERO;
import static java.time.Duration.ofHours;
import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("JUnitTestMethodWithNoAssertions")
final class DurationCodecsTests extends AbstractCodecsTests {

    private DurationCodecsTests() {}

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

        try (BasicOutputBuffer output = new BasicOutputBuffer()) {
            Duration duration = Duration.ofDays(455);
            encode(output, codec, duration);

            Duration decoded = decode(wrap(output.toByteArray()), codec);

//            assertEquals(value, decoded);
        }


    }
}
