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

import static ionutbalosin.training.ecommerce.order.KafkaContainerConfiguration.consumerConfigs;
import static ionutbalosin.training.ecommerce.order.listener.OrderEventListener.ORDERS_TOPIC;
import static ionutbalosin.training.ecommerce.order.listener.OrderEventListener.PAYMENTS_IN_TOPIC;
import static java.util.List.of;
import static java.util.UUID.fromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import ionutbalosin.training.ecommerce.message.schema.currency.Currency;
import ionutbalosin.training.ecommerce.message.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentTriggerCommand;
import ionutbalosin.training.ecommerce.message.schema.product.ProductEvent;
import ionutbalosin.training.ecommerce.order.KafkaContainerConfiguration;
import ionutbalosin.training.ecommerce.order.KafkaSingletonContainer;
import ionutbalosin.training.ecommerce.order.PostgresqlSingletonContainer;
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
public class OrderEventListenerTest {

  private final UUID PREFILLED_USER_ID = fromString("42424242-4242-4242-4242-424242424242");

  private final ProductEvent PRODUCT_EVENT = getProductEvent();
  private final OrderCreatedEvent ORDER_CREATED = getOrderCreatedEvent();

  @Container
  private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER =
      PostgresqlSingletonContainer.INSTANCE.getContainer();

  @Container
  private static final KafkaContainer KAFKA_CONTAINER =
      KafkaSingletonContainer.INSTANCE.getContainer();

  @Autowired private OrderEventListener classUnderTest;
  @Autowired private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

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
    final KafkaConsumer<String, PaymentTriggerCommand> kafkaConsumer =
        new KafkaConsumer(consumerConfigs());
    kafkaConsumer.subscribe(of(PAYMENTS_IN_TOPIC));

    kafkaTemplate.send(ORDERS_TOPIC, ORDER_CREATED);

    await()
        .atMost(20, TimeUnit.SECONDS)
        .until(
            () -> {
              final ConsumerRecords<String, PaymentTriggerCommand> records =
                  kafkaConsumer.poll(Duration.ofMillis(500));
              if (records.isEmpty()) {
                return false;
              }

              assertEquals(1, records.count());
              records.forEach(
                  record -> {
                    assertNotNull(record.value().getId());
                    assertNotNull(record.value().getOrderId());
                    assertEquals(ORDER_CREATED.getUserId(), record.value().getUserId());
                    assertEquals(ORDER_CREATED.getAmount(), record.value().getAmount());
                    assertEquals(Currency.EUR, record.value().getCurrency());
                  });
              return true;
            });

    kafkaConsumer.unsubscribe();
    kafkaConsumer.close();
  }

  private ProductEvent getProductEvent() {
    final ProductEvent event = new ProductEvent();
    event.setProductId(fromString("02f85436-397f-11ed-a261-0242ac120002"));
    event.setName("Präsident Ganze Bohne");
    event.setBrand("Julius Meinl");
    event.setPrice(11);
    event.setCurrency(Currency.EUR);
    event.setQuantity(111);
    event.setDiscount(1);
    return event;
  }

  private OrderCreatedEvent getOrderCreatedEvent() {
    final OrderCreatedEvent event = new OrderCreatedEvent();
    event.setId(fromString("0b9b15a6-397f-11ed-a261-0242ac120002"));
    event.setUserId(PREFILLED_USER_ID);
    event.setProducts(List.of(PRODUCT_EVENT));
    event.setCurrency(Currency.EUR);
    event.setAmount(22);
    return event;
  }
}
