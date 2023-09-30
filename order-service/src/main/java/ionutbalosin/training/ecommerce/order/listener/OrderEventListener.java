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

import ionutbalosin.training.ecommerce.message.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentTriggerCommand;
import ionutbalosin.training.ecommerce.order.event.builder.PaymentEventBuilder;
import ionutbalosin.training.ecommerce.order.model.Order;
import ionutbalosin.training.ecommerce.order.model.mapper.OrderMapper;
import ionutbalosin.training.ecommerce.order.service.OrderService;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
public class OrderEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventListener.class);

  public static final String ORDERS_TOPIC = "ecommerce-orders-topic";
  public static final String PAYMENTS_IN_TOPIC = "ecommerce-payments-in-topic";

  private final OrderService orderService;
  private final OrderMapper orderMapper;
  private final PaymentEventBuilder paymentEventBuilder;

  public OrderEventListener(
      OrderService orderService, OrderMapper orderMapper, PaymentEventBuilder paymentEventBuilder) {
    this.orderService = orderService;
    this.orderMapper = orderMapper;
    this.paymentEventBuilder = paymentEventBuilder;
  }

  @KafkaListener(topics = ORDERS_TOPIC, groupId = "ecommerce_group_id")
  @SendTo(PAYMENTS_IN_TOPIC)
  public PaymentTriggerCommand receive(OrderCreatedEvent orderEvent) {
    LOGGER.debug("Received message '{}' from Kafka topic '{}'", orderEvent, ORDERS_TOPIC);
    final Order order = orderMapper.map(orderEvent);
    final UUID orderId = orderService.createOrder(order);

    final PaymentTriggerCommand command = paymentEventBuilder.createCommand(orderId, order);
    LOGGER.debug("Produce message '{}' to Kafka topic '{}'", command, PAYMENTS_IN_TOPIC);
    return command;
  }
}
