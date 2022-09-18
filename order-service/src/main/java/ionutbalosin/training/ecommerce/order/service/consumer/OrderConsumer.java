package ionutbalosin.training.ecommerce.order.service.consumer;

import ionutbalosin.training.ecommerce.event.schema.order.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

  private static final String TOPIC = "ecommerce-orders-topic";

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

  @KafkaListener(topics = TOPIC, groupId = "ecommerce_group_id")
  public void consume(OrderCreatedEvent message) {
    LOGGER.info(String.format("#### -> Consumed message -> %s", message));
  }
}
