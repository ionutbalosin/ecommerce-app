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
import static ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus.FAILED;
import static ionutbalosin.training.ecommerce.order.model.OrderStatus.PAYMENT_FAILED;
import static ionutbalosin.training.ecommerce.order.model.OrderStatus.SHIPPING_TRIGGERED;

import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatusUpdatedEvent;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentTriggeredEvent;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingTriggerCommand;
import ionutbalosin.training.ecommerce.order.event.builder.PaymentEventBuilder;
import ionutbalosin.training.ecommerce.order.event.builder.ShippingEventBuilder;
import ionutbalosin.training.ecommerce.order.model.Order;
import ionutbalosin.training.ecommerce.order.model.mapper.OrderMapper;
import ionutbalosin.training.ecommerce.order.sender.ShippingEventSender;
import ionutbalosin.training.ecommerce.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentEventListener.class);

  public static final String PAYMENTS_OUT_TOPIC = "ecommerce-payments-out-topic";
  public static final String SHIPPING_IN_TOPIC = "ecommerce-shipping-in-topic";
  public static final String NOTIFICATIONS_TOPIC = "ecommerce-notifications-topic";

  private final OrderMapper orderMapper;
  private final OrderService orderService;
  private final PaymentEventBuilder paymentEventBuilder;
  private final ShippingEventBuilder shippingEventBuilder;
  private final ShippingEventSender shippingEventSender;

  public PaymentEventListener(
      OrderMapper orderMapper,
      OrderService orderService,
      PaymentEventBuilder paymentEventBuilder,
      ShippingEventBuilder shippingEventBuilder,
      ShippingEventSender shippingEventSender) {
    this.orderMapper = orderMapper;
    this.orderService = orderService;
    this.paymentEventBuilder = paymentEventBuilder;
    this.shippingEventBuilder = shippingEventBuilder;
    this.shippingEventSender = shippingEventSender;
  }

  @KafkaListener(topics = PAYMENTS_OUT_TOPIC, groupId = "ecommerce_group_id_id_ack")
  @SendTo(NOTIFICATIONS_TOPIC)
  public PaymentStatusUpdatedEvent receive(PaymentTriggeredEvent paymentEvent) {
    LOGGER.debug("Received message '{}' from Kafka topic '{}'", paymentEvent, PAYMENTS_OUT_TOPIC);
    final Order order = orderService.getOrder(paymentEvent.getOrderId());

    if (paymentEvent.getStatus() == FAILED) {
      // update order to status PAYMENT_FAILED
      final Order orderUpdate = orderMapper.map(order, PAYMENT_FAILED);
      orderService.updateOrder(orderUpdate);
      LOGGER.debug(
          "Order id '{}' for user id '{}' was updated to status '{}'",
          orderUpdate.getId(),
          orderUpdate.getUserId(),
          orderUpdate.getStatus());
    } else if (paymentEvent.getStatus() == APPROVED) {
      // create and send the shipping command
      final ShippingTriggerCommand shippingTriggerCommand =
          shippingEventBuilder.createCommand(order);
      shippingEventSender.send(shippingTriggerCommand);
      // update order to status SHIPPING_INITIATED
      final Order orderUpdate = orderMapper.map(order, SHIPPING_TRIGGERED);
      orderService.updateOrder(orderUpdate);
      LOGGER.debug(
          "Order id '{}' for user id '{}' was updated to status '{}'",
          orderUpdate.getId(),
          orderUpdate.getUserId(),
          orderUpdate.getStatus());
    }

    final PaymentStatusUpdatedEvent event =
        paymentEventBuilder.createEvent(order, paymentEvent.getStatus());
    LOGGER.debug("Produce message '{}' to Kafka topic '{}'", event, NOTIFICATIONS_TOPIC);
    return event;
  }
}
