package ionutbalosin.training.ecommerce.order.service;

import static ionutbalosin.training.ecommerce.message.schema.payment.PaymentCurrency.valueOf;
import static java.util.UUID.randomUUID;

import ionutbalosin.training.ecommerce.message.schema.payment.TriggerPaymentCommand;
import ionutbalosin.training.ecommerce.order.model.Order;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventBuilder {

  public TriggerPaymentCommand createCommand(UUID orderId, Order order) {
    final TriggerPaymentCommand command = new TriggerPaymentCommand();
    command.setId(randomUUID());
    command.setOrderId(orderId);
    command.setUserId(order.getUserId());
    command.setDescription("Payment for user id " + order.getUserId());
    command.setAmount(order.getAmount());
    command.setCurrency(valueOf(order.getCurrency()));
    return command;
  }
}
