package ionutbalosin.training.ecommerce.shopping.cart.util;

import static java.time.ZoneOffset.UTC;
import static java.util.Optional.ofNullable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
public class DateUtil {

  public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

  public static LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
    return ofNullable(timestamp)
        .map(Timestamp::toInstant)
        .map(instant -> instant.atZone(UTC_ZONE_ID))
        .map(ZonedDateTime::toLocalDateTime)
        .orElse(null);
  }

  public static Timestamp localDateTimeToTimestamp(LocalDateTime localDateTime) {
    return ofNullable(localDateTime)
        .map(dateTime -> dateTime.toInstant(UTC))
        .map(Timestamp::from)
        .orElse(null);
  }
}
