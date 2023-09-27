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
package ionutbalosin.training.ecommerce.order.listener;

import static ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus.APPROVED;

import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatusUpdatedEvent;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentTriggeredEvent;
import ionutbalosin.training.ecommerce.order.event.builder.PaymentEventBuilder;
import ionutbalosin.training.ecommerce.order.model.Order;
import ionutbalosin.training.ecommerce.order.model.mapper.OrderMapper;
import ionutbalosin.training.ecommerce.order.sender.PaymentEventSender;
import ionutbalosin.training.ecommerce.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentEventListener.class);

  public static final String PAYMENTS_OUT_TOPIC = "ecommerce-payments-out-topic";
  public static final String SHIPPING_TOPIC = "ecommerce-shipping-topic";
  public static final String NOTIFICATIONS_TOPIC = "ecommerce-notifications-topic";

  private final OrderMapper orderMapper;
  private final OrderService orderService;
  private final PaymentEventBuilder paymentEventBuilder;
  private final PaymentEventSender paymentEventSender;

  public PaymentEventListener(
      OrderMapper orderMapper,
      OrderService orderService,
      PaymentEventSender paymentEventSender,
      PaymentEventBuilder paymentEventBuilder) {
    this.orderMapper = orderMapper;
    this.orderService = orderService;
    this.paymentEventBuilder = paymentEventBuilder;
    this.paymentEventSender = paymentEventSender;
  }

  @KafkaListener(topics = PAYMENTS_OUT_TOPIC, groupId = "ecommerce_group_id_id_ack")
  public void receive(PaymentTriggeredEvent paymentEvent) {
    LOGGER.debug("Received message '{}' from Kafka topic '{}'", paymentEvent, PAYMENTS_OUT_TOPIC);
    final Order orderUpdate = orderMapper.map(paymentEvent);
    orderService.updateOrder(orderUpdate);
    LOGGER.debug(
        "Order id '{}' for user id '{}' was updated to status '{}'",
        orderUpdate.getId(),
        orderUpdate.getUserId(),
        orderUpdate.getStatus());

    final Order order = orderService.getOrder(orderUpdate.getId());
    final PaymentStatusUpdatedEvent paymentStatusUpdatedEvent =
        paymentEventBuilder.createEvent(order, paymentEvent.getStatus());

    paymentEventSender.send(NOTIFICATIONS_TOPIC, paymentStatusUpdatedEvent);
    if (paymentEvent.getStatus() == APPROVED) {
      paymentEventSender.send(SHIPPING_TOPIC, paymentStatusUpdatedEvent);
    }
  }
}
