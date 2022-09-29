package ionutbalosin.training.ecommerce.order.service;

import ionutbalosin.training.ecommerce.message.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.message.schema.payment.TriggerPaymentCommand;
import ionutbalosin.training.ecommerce.order.dto.mapper.OrderMapper;
import ionutbalosin.training.ecommerce.order.model.Order;
import java.util.UUID;
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
  public TriggerPaymentCommand consume(OrderCreatedEvent orderEvent) {
    LOGGER.debug("Consumed message '{}' from Kafka topic '{}'", orderEvent, ORDERS_TOPIC);
    final Order order = orderMapper.map(orderEvent);
    final UUID orderId = orderService.createOrder(order);
    final TriggerPaymentCommand paymentEvent = paymentEventBuilder.createCommand(orderId, order);
    LOGGER.debug("Produce message '{}' to Kafka topic '{}'", paymentEvent, PAYMENTS_IN_TOPIC);
    return paymentEvent;
  }
}
