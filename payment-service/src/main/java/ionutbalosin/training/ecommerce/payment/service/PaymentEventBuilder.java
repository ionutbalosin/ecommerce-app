package ionutbalosin.training.ecommerce.payment.service;

import static java.util.UUID.randomUUID;

import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentTriggeredEvent;
import ionutbalosin.training.ecommerce.payment.model.Payment;
import org.springframework.stereotype.Service;

@Service
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
