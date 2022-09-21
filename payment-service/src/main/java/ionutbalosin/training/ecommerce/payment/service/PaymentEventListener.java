package ionutbalosin.training.ecommerce.payment.service;

import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentTriggeredEvent;
import ionutbalosin.training.ecommerce.message.schema.payment.TriggerPaymentCommand;
import ionutbalosin.training.ecommerce.payment.dto.mapper.PaymentMapper;
import ionutbalosin.training.ecommerce.payment.dto.mapper.PaymentStatusMapper;
import ionutbalosin.training.ecommerce.payment.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventListener {

  private static final String PAYMENTS_IN_TOPIC = "ecommerce-payments-in-topic";
  private static final String PAYMENTS_OUT_TOPIC = "ecommerce-payments-out-topic";

  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentEventListener.class);

  private final PaymentMapper paymentMapper;
  private final PaymentStatusMapper paymentStatusMapper;
  private final PaymentService paymentService;
  private final PaymentEventBuilder paymentEventBuilder;

  public PaymentEventListener(
      PaymentMapper paymentMapper,
      PaymentStatusMapper paymentStatusMapper,
      PaymentService paymentService,
      PaymentEventBuilder paymentEventBuilder) {
    this.paymentMapper = paymentMapper;
    this.paymentStatusMapper = paymentStatusMapper;
    this.paymentService = paymentService;
    this.paymentEventBuilder = paymentEventBuilder;
  }

  @KafkaListener(topics = PAYMENTS_IN_TOPIC, groupId = "ecommerce_group_id")
  @SendTo(PAYMENTS_OUT_TOPIC)
  public PaymentTriggeredEvent consume(TriggerPaymentCommand paymentCommand) {
    LOGGER.info("Consumed message: {}", paymentCommand);
    final Payment payment = paymentMapper.map(paymentCommand);
    final HttpStatus httpStatus = paymentService.pay(payment);
    final PaymentStatus paymentStatus = paymentStatusMapper.map(httpStatus);
    final PaymentTriggeredEvent paymentEvent =
        paymentEventBuilder.createEvent(payment, paymentStatus);
    LOGGER.info("Produced message: {}", paymentEvent);
    return paymentEvent;
  }
}
