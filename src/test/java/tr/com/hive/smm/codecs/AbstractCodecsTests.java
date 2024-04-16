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

import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.io.BasicOutputBuffer;
import org.bson.io.BsonOutput;

import java.nio.ByteBuffer;

import static java.nio.ByteBuffer.wrap;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractCodecsTests {

    protected AbstractCodecsTests() {}

    protected static <T> void testCodec(Codec<T> codec, T value) {
        try (BasicOutputBuffer output = new BasicOutputBuffer()) {

            encode(output, codec, value);

            T decoded = decode(wrap(output.toByteArray()), codec);

            assertEquals(value, decoded);
        }
    }

    protected static <T> void encode(BsonOutput output, Codec<T> codec, T value) {
        try (BsonBinaryWriter writer = new BsonBinaryWriter(output)) {

            writer.writeStartDocument();
            writer.writeName("value");

            codec.encode(writer, value, EncoderContext.builder().build());

            writer.writeEndDocument();
        }
    }

    protected static <T> T decode(ByteBuffer byteBuffer, Codec<T> codec) {
        try (BsonBinaryReader reader = new BsonBinaryReader(byteBuffer)) {

            reader.readStartDocument();

            assertEquals("value", reader.readName());

            return codec.decode(reader, DecoderContext.builder().build());
        }
    }

    protected static Document decodeToDocument(ByteBuffer byteBuffer, SMMCodec<?> codec) {
        try (BsonBinaryReader reader = new BsonBinaryReader(byteBuffer)) {
            return CodecsUtil.readDocument(reader, DecoderContext.builder().build(), codec.getFieldDecoders());
        }
    }

    protected static <T> void codecEncode(SMMCodec<T> codec, T t, BasicOutputBuffer output){
        try (BsonBinaryWriter writer = new BsonBinaryWriter(output)) {
            codec.encode(writer, t, EncoderContext.builder().build());
        }
    }

    protected static <T> Document encodeDecodeAndGetDocument(T t, SMMCodec<T> codec){
        try (BasicOutputBuffer output = new BasicOutputBuffer()) {
            codecEncode(codec, t, output);
            return decodeToDocument(wrap(output.toByteArray()), codec);
        }
    }
}
