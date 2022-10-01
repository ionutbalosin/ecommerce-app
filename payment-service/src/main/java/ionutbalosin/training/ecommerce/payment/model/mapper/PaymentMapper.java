package ionutbalosin.training.ecommerce.payment.model.mapper;

import static ionutbalosin.training.ecommerce.payment.model.Payment.PaymentCurrency.fromValue;

import ionutbalosin.training.ecommerce.message.schema.payment.TriggerPaymentCommand;
import ionutbalosin.training.ecommerce.payment.model.Payment;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
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
