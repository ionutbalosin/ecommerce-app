package ionutbalosin.training.ecommerce.payment.dto.mapper;

import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus;
import org.springframework.http.HttpStatus;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
public class PaymentStatusMapper {

  public PaymentStatus map(HttpStatus status) {
    if (status.is2xxSuccessful()) {
      return PaymentStatus.APPROVED;
    }
    return PaymentStatus.FAILED;
  }
}
