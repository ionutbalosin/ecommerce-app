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
package ionutbalosin.training.ecommerce.shipping.adapter.listener;

import static ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus.COMPLETED;
import static ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus.FAILED;
import static ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus.IN_PROGRESS;
import static ionutbalosin.training.ecommerce.shipping.adapter.KafkaContainerConfig.consumerConfigs;
import static ionutbalosin.training.ecommerce.shipping.adapter.listener.ShippingEventListenerAdapter.SHIPPING_IN_TOPIC;
import static ionutbalosin.training.ecommerce.shipping.adapter.listener.ShippingEventListenerAdapter.SHIPPING_OUT_TOPIC;
import static java.util.List.of;
import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import ionutbalosin.training.ecommerce.message.schema.currency.Currency;
import ionutbalosin.training.ecommerce.message.schema.product.ProductEvent;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatusUpdatedEvent;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingTriggerCommand;
import ionutbalosin.training.ecommerce.shipping.adapter.KafkaContainerConfig;
import ionutbalosin.training.ecommerce.shipping.adapter.KafkaSingletonContainer;
import ionutbalosin.training.ecommerce.shipping.adapter.ShippingEventListenerConfig;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest(
    properties = {"shipping.delay.in.sec=3"},
    classes = ShippingEventListenerConfig.class)
@Import(KafkaContainerConfig.class)
public class ShippingEventListenerAdapterTest {

  private final UUID USER_ID = fromString("fdc888dc-39ba-11ed-a261-0242ac120002");
  private final UUID ORDER_ID = fromString("fdc881e8-39ba-11ed-a261-0242ac120002");
  private final ProductEvent PRODUCT_EVENT = getProductEvent();
  private final ShippingTriggerCommand SHIPPING_TRIGGER = getShippingTriggerCommand();
  private final ShippingStatusUpdatedEvent SHIPPING_TRIGGERED = getShippingStatusUpdatedEvent();

  @Container
  private static final KafkaContainer KAFKA_CONTAINER =
      KafkaSingletonContainer.INSTANCE.getContainer();

  @Autowired private ShippingEventListenerAdapter classUnderTest;
  @Autowired private KafkaTemplate<String, ShippingTriggerCommand> kafkaTemplate;

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
    final KafkaConsumer<String, ShippingStatusUpdatedEvent> kafkaConsumer =
        new KafkaConsumer(consumerConfigs());
    kafkaConsumer.subscribe(of(SHIPPING_OUT_TOPIC));

    kafkaTemplate.send(SHIPPING_IN_TOPIC, SHIPPING_TRIGGER);

    final List<ConsumerRecord<String, ShippingStatusUpdatedEvent>> allRecords = new ArrayList<>();
    await()
        .atMost(20, TimeUnit.SECONDS)
        .until(
            () -> {
              final ConsumerRecords<String, ShippingStatusUpdatedEvent> records =
                  kafkaConsumer.poll(Duration.ofMillis(500));
              if (!records.isEmpty()) {
                records.forEach(allRecords::add);
              }

              return allRecords.size() == 2;
            });

    assertEquals(2, allRecords.size());
    allRecords.forEach(
        record -> {
          assertNotNull(record.value().getId());
          assertNotNull(record.value().getStatus());
          assertEquals(SHIPPING_TRIGGERED.getUserId(), record.value().getUserId());
          assertEquals(SHIPPING_TRIGGERED.getOrderId(), record.value().getOrderId());
          assertTrue(List.of(IN_PROGRESS, FAILED, COMPLETED).contains(record.value().getStatus()));
        });

    kafkaConsumer.unsubscribe();
    kafkaConsumer.close();
  }

  public ShippingStatusUpdatedEvent getShippingStatusUpdatedEvent() {
    final ShippingStatusUpdatedEvent event = new ShippingStatusUpdatedEvent();
    event.setId(randomUUID());
    event.setOrderId(ORDER_ID);
    event.setUserId(USER_ID);
    event.setStatus(ShippingStatus.IN_PROGRESS);
    return event;
  }

  private ShippingTriggerCommand getShippingTriggerCommand() {
    final ShippingTriggerCommand command = new ShippingTriggerCommand();
    command.setId(randomUUID());
    command.setUserId(USER_ID);
    command.setOrderId(ORDER_ID);
    command.setAmount(33.0);
    command.setProducts(List.of(PRODUCT_EVENT));
    command.setCurrency(Currency.EUR);
    return command;
  }

  private ProductEvent getProductEvent() {
    final ProductEvent event = new ProductEvent();
    event.setProductId(fromString("46414ebe-5dcd-11ee-8c99-0242ac120002"));
    event.setName("Blue Bottle Coffee");
    event.setBrand("Ethiopia Yirgacheffe Coffee");
    event.setPrice(11);
    event.setCurrency(Currency.EUR);
    event.setQuantity(111);
    return event;
  }
}
