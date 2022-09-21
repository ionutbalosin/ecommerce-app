package ionutbalosin.training.ecommerce.order.service;

import static ionutbalosin.training.ecommerce.message.schema.payment.PaymentCurrency.valueOf;
import static java.util.UUID.randomUUID;

import ionutbalosin.training.ecommerce.message.schema.payment.TriggerPaymentCommand;
import ionutbalosin.training.ecommerce.order.model.Order;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventBuilder {

  public TriggerPaymentCommand createEvent(UUID orderId, Order order) {
    final TriggerPaymentCommand event = new TriggerPaymentCommand();
    event.setId(randomUUID());
    event.setOrderId(orderId);
    event.setUserId(order.getUserId());
    event.setDescription("Payment for user id " + order.getUserId());
    event.setAmount(order.getAmount());
    event.setCurrency(valueOf(order.getCurrency()));
    return event;
  }
}
