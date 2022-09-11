package ionutbalosin.training.ecommerce.product.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

public class DateUtil {

  public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

  public static LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
    return Optional.ofNullable(timestamp)
        .map(Timestamp::toInstant)
        .map(instant -> instant.atZone(UTC_ZONE_ID))
        .map(ZonedDateTime::toLocalDateTime)
        .orElse(null);
  }

  public static Timestamp localDateTimeToTimestamp(LocalDateTime localDateTime) {
    return Optional.ofNullable(localDateTime)
        .map(dateTime -> dateTime.toInstant(ZoneOffset.UTC))
        .map(Timestamp::from)
        .orElse(null);
  }
}
