package ionutbalosin.training.ecommerce.order.service.producer;

import ionutbalosin.training.ecommerce.event.schema.order.OrderCreatedEvent;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderProducer.class);

  private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
  private static final String TOPIC = "ecommerce-orders-topic";

  public OrderProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void produce() {
    final OrderCreatedEvent orderCreated = new OrderCreatedEvent();
    orderCreated.setUserId(UUID.randomUUID());

    LOGGER.info(String.format("#### -> Produce message -> %s", orderCreated));
    kafkaTemplate.send(TOPIC, orderCreated);
  }
}
