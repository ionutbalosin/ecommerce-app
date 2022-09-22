package ionutbalosin.training.ecommerce.shopping.cart.service;

import ionutbalosin.training.ecommerce.message.schema.order.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class OrderEventSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventSender.class);
  private static final String TOPIC = "ecommerce-orders-topic";

  private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

  public OrderEventSender(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendEvent(OrderCreatedEvent event) {
    final ListenableFuture<SendResult<String, OrderCreatedEvent>> future =
        kafkaTemplate.send(TOPIC, event);
    future.addCallback(
        new KafkaSendCallback<>() {
          @Override
          public void onSuccess(SendResult<String, OrderCreatedEvent> result) {}

          @Override
          public void onFailure(KafkaProducerException e) {
            LOGGER.error(
                "Unable to send event '{}' to Kafka topic '{}', exception '{}'", event, TOPIC, e);
          }
        });
  }
}
