package ionutbalosin.training.ecommerce.product.util;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
public class NumberUtil {

  public static BigDecimal fromFloat(float value) {
    return valueOf(value).setScale(2, RoundingMode.HALF_UP);
  }
}
