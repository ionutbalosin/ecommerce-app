package ionutbalosin.training.ecommerce.payment.listener;

import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentTriggeredEvent;
import ionutbalosin.training.ecommerce.message.schema.payment.TriggerPaymentCommand;
import ionutbalosin.training.ecommerce.payment.event.builder.PaymentEventBuilder;
import ionutbalosin.training.ecommerce.payment.model.Payment;
import ionutbalosin.training.ecommerce.payment.model.mapper.PaymentMapper;
import ionutbalosin.training.ecommerce.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
@Service
public class PaymentEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentEventListener.class);

  public static final String PAYMENTS_IN_TOPIC = "ecommerce-payments-in-topic";
  public static final String PAYMENTS_OUT_TOPIC = "ecommerce-payments-out-topic";

  private final PaymentMapper paymentMapper;
  private final PaymentService paymentService;
  private final PaymentEventBuilder paymentEventBuilder;

  public PaymentEventListener(
      PaymentMapper paymentMapper,
      PaymentService paymentService,
      PaymentEventBuilder paymentEventBuilder) {
    this.paymentMapper = paymentMapper;
    this.paymentService = paymentService;
    this.paymentEventBuilder = paymentEventBuilder;
  }

  @KafkaListener(topics = PAYMENTS_IN_TOPIC, groupId = "ecommerce_group_id")
  @SendTo(PAYMENTS_OUT_TOPIC)
  public PaymentTriggeredEvent receive(TriggerPaymentCommand paymentCommand) {
    LOGGER.debug("Received message '{}' from Kafka topic '{}'", paymentCommand, PAYMENTS_IN_TOPIC);
    final Payment payment = paymentMapper.map(paymentCommand);
    final PaymentStatus paymentStatus = paymentService.triggerPayment(payment);
    final PaymentTriggeredEvent paymentEvent =
        paymentEventBuilder.createEvent(payment, paymentStatus);
    LOGGER.debug("Produce message '{}' to Kafka topic '{}'", paymentEvent, PAYMENTS_OUT_TOPIC);
    return paymentEvent;
  }
}
