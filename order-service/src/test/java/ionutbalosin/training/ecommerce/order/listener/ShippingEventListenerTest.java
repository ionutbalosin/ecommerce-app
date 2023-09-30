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

import static ionutbalosin.training.ecommerce.message.schema.order.OrderStatus.SHIPPING_IN_PROGRESS;
import static ionutbalosin.training.ecommerce.order.KafkaContainerConfiguration.consumerConfigs;
import static ionutbalosin.training.ecommerce.order.listener.PaymentEventListener.NOTIFICATIONS_TOPIC;
import static ionutbalosin.training.ecommerce.order.listener.ShippingEventListener.SHIPPING_OUT_TOPIC;
import static java.util.List.of;
import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import ionutbalosin.training.ecommerce.message.schema.currency.Currency;
import ionutbalosin.training.ecommerce.message.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.message.schema.order.OrderStatusUpdatedEvent;
import ionutbalosin.training.ecommerce.message.schema.product.ProductEvent;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatusUpdatedEvent;
import ionutbalosin.training.ecommerce.order.KafkaContainerConfiguration;
import ionutbalosin.training.ecommerce.order.KafkaSingletonContainer;
import ionutbalosin.training.ecommerce.order.PostgresqlSingletonContainer;
import ionutbalosin.training.ecommerce.order.model.Order;
import ionutbalosin.training.ecommerce.order.model.mapper.OrderMapper;
import ionutbalosin.training.ecommerce.order.service.OrderService;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest()
@Import(KafkaContainerConfiguration.class)
public class ShippingEventListenerTest {

  private final UUID PREFILLED_USER_ID = fromString("42424242-4242-4242-4242-424242424242");

  private final ProductEvent PRODUCT_EVENT = getProductEvent();
  private final OrderCreatedEvent ORDER_CREATED = getOrderCreatedEvent();

  @Container
  private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER =
      PostgresqlSingletonContainer.INSTANCE.getContainer();

  @Container
  private static final KafkaContainer KAFKA_CONTAINER =
      KafkaSingletonContainer.INSTANCE.getContainer();

  @Autowired private ShippingEventListener classUnderTest;
  @Autowired private KafkaTemplate<String, ShippingStatusUpdatedEvent> kafkaTemplate;
  @Autowired private OrderService orderService;
  @Autowired private OrderMapper orderMapper;

  @BeforeAll
  public static void setUp() {
    KafkaSingletonContainer.INSTANCE.start();
  }

  @AfterAll
  public static void tearDown() {
    KafkaSingletonContainer.INSTANCE.stop();
  }

  @Test
  @DirtiesContext
  public void receive() {
    // pre-fill the database with the orders we need to process the payment status update later on
    final Order order = orderMapper.map(ORDER_CREATED);
    final UUID orderId = orderService.createOrder(order);

    final ShippingStatusUpdatedEvent shippingStatusUpdatedEvent =
        getShippingStatusUpdatedEvent(orderId);

    final KafkaConsumer<String, OrderStatusUpdatedEvent> kafkaConsumer =
        new KafkaConsumer(consumerConfigs());
    kafkaConsumer.subscribe(of(NOTIFICATIONS_TOPIC));

    kafkaTemplate.send(SHIPPING_OUT_TOPIC, shippingStatusUpdatedEvent);

    await()
        .atMost(20, TimeUnit.SECONDS)
        .until(
            () -> {
              final ConsumerRecords<String, OrderStatusUpdatedEvent> records =
                  kafkaConsumer.poll(Duration.ofMillis(500));
              if (records.isEmpty()) {
                return false;
              }

              assertEquals(1, records.count());
              records.forEach(
                  record -> {
                    assertNotNull(record.value().getId());
                    assertEquals(orderId, record.value().getOrderId());
                    assertEquals(ORDER_CREATED.getUserId(), record.value().getUserId());
                    assertNotNull(record.value().getProducts());
                    assertEquals(1, record.value().getProducts().size());
                    assertEquals(ORDER_CREATED.getAmount(), record.value().getAmount());
                    assertEquals(ORDER_CREATED.getCurrency(), record.value().getCurrency());
                    assertEquals(SHIPPING_IN_PROGRESS, record.value().getStatus());
                  });
              return true;
            });

    kafkaConsumer.unsubscribe();
    kafkaConsumer.close();
  }

  public ShippingStatusUpdatedEvent getShippingStatusUpdatedEvent(UUID orderId) {
    final ShippingStatusUpdatedEvent event = new ShippingStatusUpdatedEvent();
    event.setId(randomUUID());
    event.setOrderId(orderId);
    event.setUserId(PREFILLED_USER_ID);
    event.setStatus(ShippingStatus.IN_PROGRESS);
    return event;
  }

  private ProductEvent getProductEvent() {
    final ProductEvent event = new ProductEvent();
    event.setProductId(fromString("162ea518-5e1e-11ee-8c99-0242ac120002"));
    event.setName("Nespresso");
    event.setBrand("Arpeggio");
    event.setPrice(33);
    event.setCurrency(Currency.EUR);
    event.setQuantity(333);
    event.setDiscount(3);
    return event;
  }

  private OrderCreatedEvent getOrderCreatedEvent() {
    final OrderCreatedEvent event = new OrderCreatedEvent();
    event.setId(fromString("041ca6ae-5e1e-11ee-8c99-0242ac120002"));
    event.setUserId(PREFILLED_USER_ID);
    event.setProducts(List.of(PRODUCT_EVENT));
    event.setCurrency(Currency.EUR);
    event.setAmount(11.0);
    return event;
  }
}
