package tr.com.hive.smm.codecs.internal;

import org.bson.codecs.Codec;
import org.bson.codecs.pojo.PropertyCodecProvider;
import org.bson.codecs.pojo.PropertyCodecRegistry;
import org.bson.codecs.pojo.TypeWithTypeParameters;

import java.util.Map;

public class MapCodecProvider implements PropertyCodecProvider {

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public <T> Codec<T> get(final TypeWithTypeParameters<T> type, final PropertyCodecRegistry registry) {
    if (Map.class.isAssignableFrom(type.getType()) && type.getTypeParameters().size() == 2) {
      TypeWithTypeParameters<?> keyType = type.getTypeParameters().get(0);

      return new MapCodec(
        type.getType(),
        registry.get(type.getTypeParameters().get(1)),
        keyType
      );
    }

    return null;
  }

}
