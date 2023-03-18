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
package ionutbalosin.training.ecommerce.shopping.cart.listener;

import ionutbalosin.training.ecommerce.message.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.shopping.cart.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/*
 * This listener implements the "Listen to yourself" pattern to mitigate the issues
 * that might occur while receiving a request and performing "atomic" operations between a database
 * and a topic. In our use case we listen to published ORDER_CREATED event to remove the user's
 * items (i.e., if the message was already published to the message broker, we assume a consistent
 * state)
 * References:
 * https://medium.com/@odedia/listen-to-yourself-design-pattern-for-event-driven-microservices-16f97e3ed066
 * https://www.squer.at/en/blog/stop-overusing-the-outbox-pattern/
 */
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
