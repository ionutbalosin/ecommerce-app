package ionutbalosin.training.ecommerce.shopping.cart.sender;

import ionutbalosin.training.ecommerce.message.schema.order.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
@Service
public class OrderEventSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventSender.class);

  private static final String ORDERS_TOPIC = "ecommerce-orders-topic";

  private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

  public OrderEventSender(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(OrderCreatedEvent event) {
    final ListenableFuture<SendResult<String, OrderCreatedEvent>> future =
        kafkaTemplate.send(ORDERS_TOPIC, event);
    future.addCallback(
        new KafkaSendCallback<>() {
          @Override
          public void onSuccess(SendResult<String, OrderCreatedEvent> result) {
            LOGGER.debug("Sent message '{}' to Kafka topic '{}'", event, ORDERS_TOPIC);
          }

          @Override
          public void onFailure(KafkaProducerException e) {
            LOGGER.error(
                "Unable to send message '{}' to Kafka topic '{}', exception '{}'",
                event,
                ORDERS_TOPIC,
                e);
          }
        });
  }
}
