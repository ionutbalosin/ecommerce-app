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

@Service
public class OrderEventListener {

  private static final String ORDERS_TOPIC = "ecommerce-orders-topic";
  private static final String PAYMENTS_TOPIC = "ecommerce-payments-topic";

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventListener.class);

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
  @SendTo(PAYMENTS_TOPIC)
  public TriggerPaymentCommand consume(OrderCreatedEvent orderEvent) {
    LOGGER.info(String.format("#### -> Consumed message -> %s", orderEvent));
    final Order order = orderMapper.map(orderEvent);
    final UUID orderId = orderService.createOrder(order);
    final TriggerPaymentCommand paymentEvent = paymentEventBuilder.createEvent(orderId, order);
    LOGGER.info(String.format("#### -> Produced message -> %s", paymentEvent));
    return paymentEvent;
  }
}
