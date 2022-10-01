package ionutbalosin.training.ecommerce.shopping.cart.listener;

import ionutbalosin.training.ecommerce.message.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.shopping.cart.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
//
// This listener implements the "Listen to yourself" pattern to mitigate the issues
// that might occur while receiving a request and performing "atomic" operations between a database
// and a topic. In our use case we listen to published ORDER_CREATED event to remove the user's
// items (i.e., if the message was already published to the message broker, we assume a consistent
// state)
// References:
// https://medium.com/@odedia/listen-to-yourself-design-pattern-for-event-driven-microservices-16f97e3ed066
// https://www.squer.at/en/blog/stop-overusing-the-outbox-pattern/
@Service
public class OrderEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventListener.class);

  public static final String ORDERS_TOPIC = "ecommerce-orders-topic";

  private final CartService cartService;

  public OrderEventListener(CartService cartService) {
    this.cartService = cartService;
  }

  @KafkaListener(topics = ORDERS_TOPIC, groupId = "ecommerce_group_id_ack")
  public void receive(OrderCreatedEvent orderEvent) {
    LOGGER.debug("Received message '{}' from Kafka topic '{}'", orderEvent, ORDERS_TOPIC);
    cartService.deleteCartItems(orderEvent.getUserId());
    LOGGER.debug("Shopping cart items for user id '{}' were deleted", orderEvent.getUserId());
  }
}
