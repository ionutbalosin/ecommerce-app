package ionutbalosin.training.ecommerce.payment.event.builder;

import static java.util.UUID.randomUUID;

import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentTriggeredEvent;
import ionutbalosin.training.ecommerce.payment.model.Payment;
import org.springframework.stereotype.Component;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
@Component
public class PaymentEventBuilder {

  public PaymentTriggeredEvent createEvent(Payment payment, PaymentStatus status) {
    final PaymentTriggeredEvent event = new PaymentTriggeredEvent();
    event.setId(randomUUID());
    event.setUserId(payment.getUserId());
    event.setOrderId(payment.getOrderId());
    event.setStatus(status);
    return event;
  }
}
