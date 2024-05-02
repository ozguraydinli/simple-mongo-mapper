package tr.com.hive.smm.codecs.time;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.time.Duration;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.function.Function;

public class JavaTimeCodecProvider implements CodecProvider {

  private static final Map<Class<?>, Function<CodecRegistry, Codec<?>>> CODEC_MAP = Map.of(
//    Instant.class, registry -> new InstantCodec(),
    Duration.class, registry -> new DurationAsDocumentCodec(),
    Period.class, registry -> new PeriodAsDocumentCodec(),
    Month.class, registry -> new MonthAsInt32Codec(),
    Year.class, registry -> new YearAsInt32Codec(),
    YearMonth.class, registry -> new YearMonthAsDocumentCodec(),
    OffsetDateTime.class, OffsetDateTimeAsDocumentCodec::new,
    ZonedDateTime.class, ZonedDateTimeAsDocumentCodec::new,
    ZoneId.class, registry -> new ZoneIdAsStringCodec(),
    ZoneOffset.class, registry -> new ZoneOffsetAsInt32Codec()
  );

  @SuppressWarnings("unchecked")
  @Override
  public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
    if (CODEC_MAP.containsKey(clazz)) {
      return (Codec<T>) CODEC_MAP.get(clazz).apply(registry);
    }

    return null;
  }

  @Override
  public String toString() {
    return "JavaTimeCodecProvider{}";
  }

}
