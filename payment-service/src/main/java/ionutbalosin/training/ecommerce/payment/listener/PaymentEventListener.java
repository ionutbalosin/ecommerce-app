/**
 *  eCommerce Application
 *
 *  Copyright (c) 2022 - 2023 Ionut Balosin
 *  Website: www.ionutbalosin.com
 *  Twitter: @ionutbalosin / Mastodon: ionutbalosin@mastodon.socia
 *
 *
 *  MIT License
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */
package ionutbalosin.training.ecommerce.payment.listener;

import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatusUpdatedEvent;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentTriggerCommand;
import ionutbalosin.training.ecommerce.payment.event.builder.PaymentEventBuilder;
import ionutbalosin.training.ecommerce.payment.model.Payment;
import ionutbalosin.training.ecommerce.payment.model.mapper.PaymentMapper;
import ionutbalosin.training.ecommerce.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

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
  public PaymentStatusUpdatedEvent receive(PaymentTriggerCommand paymentCommand) {
    LOGGER.debug("Received message '{}' from Kafka topic '{}'", paymentCommand, PAYMENTS_IN_TOPIC);
    final Payment payment = paymentMapper.map(paymentCommand);
    final PaymentStatus paymentStatus = paymentService.triggerPayment(payment);
    final PaymentStatusUpdatedEvent event = paymentEventBuilder.createEvent(payment, paymentStatus);
    LOGGER.debug("Produce message '{}' to Kafka topic '{}'", event, PAYMENTS_OUT_TOPIC);
    return event;
  }
}
