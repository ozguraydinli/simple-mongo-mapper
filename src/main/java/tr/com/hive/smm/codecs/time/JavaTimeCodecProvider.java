package tr.com.hive.smm.codecs.time;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.time.Duration;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JavaTimeCodecProvider implements CodecProvider {

  private static final Map<Class<?>, Function<CodecRegistry, Codec<?>>> CODEC_MAP = new HashMap<>();

  static {
//    CODEC_MAP.put(Instant.class, registry -> new InstantCodec());
    CODEC_MAP.put(Duration.class, registry -> new DurationAsDocumentCodec());
    CODEC_MAP.put(Period.class, registry -> new PeriodAsDocumentCodec());
    CODEC_MAP.put(Month.class, registry -> new MonthAsInt32Codec());
    CODEC_MAP.put(Year.class, registry -> new YearAsInt32Codec());
    CODEC_MAP.put(YearMonth.class, registry -> new YearMonthAsDocumentCodec());
    CODEC_MAP.put(MonthDay.class, registry -> new MonthDayAsDocumentCodec());
    CODEC_MAP.put(OffsetDateTime.class, OffsetDateTimeAsDocumentCodec::new);
    CODEC_MAP.put(OffsetTime.class, OffsetTimeAsDocumentCodec::new);
    CODEC_MAP.put(ZonedDateTime.class, ZonedDateTimeAsDocumentCodec::new);
    CODEC_MAP.put(ZoneId.class, registry -> new ZoneIdAsStringCodec());
    CODEC_MAP.put(ZoneOffset.class, registry -> new ZoneOffsetAsInt32Codec());
  }

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
