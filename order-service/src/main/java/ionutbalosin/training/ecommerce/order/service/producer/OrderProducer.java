package ionutbalosin.training.ecommerce.order.service.producer;

import ionutbalosin.training.ecommerce.order.schema.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderProducer.class);

  private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
  private static final String TOPIC = "ecommerce-orders-topic";

  public OrderProducer(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void produce() {
    final OrderCreatedEvent orderCreated = new OrderCreatedEvent();
    orderCreated.setProductId(12);
    orderCreated.setUserId(55);

    LOGGER.info(String.format("#### -> Produce message -> %s", orderCreated));
    kafkaTemplate.send(TOPIC, orderCreated);
  }
}
