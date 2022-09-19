package ionutbalosin.training.ecommerce.order.service;

import static ionutbalosin.training.ecommerce.event.schema.payment.CurrencyEnumEvent.valueOf;
import static java.util.UUID.randomUUID;

import ionutbalosin.training.ecommerce.event.schema.payment.PaymentInitiatedEvent;
import ionutbalosin.training.ecommerce.order.model.Order;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventBuilder {

  public PaymentInitiatedEvent createEvent(UUID orderId, Order order) {
    final PaymentInitiatedEvent event = new PaymentInitiatedEvent();
    event.setId(randomUUID());
    event.setOrderId(orderId);
    event.setUserId(order.getUserId());
    event.setDescription("Payment for user id " + order.getUserId());
    event.setAmount(order.getAmount());
    event.setCurrency(valueOf(order.getCurrency()));
    return event;
  }
}
