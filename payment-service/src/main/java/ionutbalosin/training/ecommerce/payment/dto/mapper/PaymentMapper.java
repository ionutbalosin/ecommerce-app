package ionutbalosin.training.ecommerce.payment.dto.mapper;

import static ionutbalosin.training.ecommerce.payment.model.Payment.PaymentCurrency.fromValue;

import ionutbalosin.training.ecommerce.message.schema.payment.TriggerPaymentCommand;
import ionutbalosin.training.ecommerce.payment.model.Payment;

public class PaymentMapper {

  public Payment map(TriggerPaymentCommand paymentCommand) {
    return new Payment()
        .userId(paymentCommand.getUserId())
        .orderId(paymentCommand.getOrderId())
        .description(paymentCommand.getDescription())
        .amount(paymentCommand.getAmount())
        .currency(fromValue(paymentCommand.getCurrency().toString()));
  }
}
