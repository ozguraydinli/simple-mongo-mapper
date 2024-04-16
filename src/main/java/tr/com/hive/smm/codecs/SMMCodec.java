package tr.com.hive.smm.codecs;

import org.bson.codecs.Codec;
import org.bson.codecs.Decoder;

import java.util.Map;

public interface SMMCodec<T> extends Codec<T> {

  Map<String, Decoder<?>> getFieldDecoders();

}
