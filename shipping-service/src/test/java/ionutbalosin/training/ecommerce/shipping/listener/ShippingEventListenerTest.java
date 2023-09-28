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
package ionutbalosin.training.ecommerce.shipping.listener;

import static ionutbalosin.training.ecommerce.shipping.KafkaContainerConfiguration.consumerConfigs;
import static ionutbalosin.training.ecommerce.shipping.listener.ShippingEventListener.SHIPPING_IN_TOPIC;
import static ionutbalosin.training.ecommerce.shipping.listener.ShippingEventListener.SHIPPING_OUT_TOPIC;
import static java.util.List.of;
import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import ionutbalosin.training.ecommerce.message.schema.currency.Currency;
import ionutbalosin.training.ecommerce.message.schema.product.ProductEvent;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingTriggerCommand;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingTriggeredEvent;
import ionutbalosin.training.ecommerce.shipping.KafkaContainerConfiguration;
import ionutbalosin.training.ecommerce.shipping.KafkaSingletonContainer;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest()
@Import(KafkaContainerConfiguration.class)
public class ShippingEventListenerTest {

  private final UUID USER_ID = fromString("fdc888dc-39ba-11ed-a261-0242ac120002");
  private final UUID ORDER_ID = fromString("fdc881e8-39ba-11ed-a261-0242ac120002");
  private final ShippingTriggerCommand SHIPPING_TRIGGER = getShippingTriggerCommand();
  private final ShippingTriggeredEvent SHIPPING_TRIGGERED = getShippingTriggeredEvent();

  @Container
  private static final KafkaContainer KAFKA_CONTAINER =
      KafkaSingletonContainer.INSTANCE.getContainer();

  @Autowired private ShippingEventListener classUnderTest;
  @Autowired private KafkaTemplate<String, ShippingTriggerCommand> kafkaTemplate;

  @Test
  public void receive() {
    final KafkaConsumer<String, ShippingTriggeredEvent> kafkaConsumer =
        new KafkaConsumer(consumerConfigs());
    kafkaConsumer.subscribe(of(SHIPPING_OUT_TOPIC));

    kafkaTemplate.send(SHIPPING_IN_TOPIC, SHIPPING_TRIGGER);

    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              final ConsumerRecords<String, ShippingTriggeredEvent> records =
                  kafkaConsumer.poll(Duration.ofMillis(500));
              if (records.isEmpty()) {
                return false;
              }

              assertEquals(1, records.count());
              records.forEach(
                  record -> {
                    assertNotNull(record.value().getId());
                    assertNotNull(record.value().getStatus());
                    assertEquals(SHIPPING_TRIGGERED.getUserId(), record.value().getUserId());
                    assertEquals(SHIPPING_TRIGGERED.getOrderId(), record.value().getOrderId());
                    assertEquals(SHIPPING_TRIGGERED.getStatus(), record.value().getStatus());
                  });
              return true;
            });

    kafkaConsumer.unsubscribe();
  }

  private ShippingTriggeredEvent getShippingTriggeredEvent() {
    final ShippingTriggeredEvent event = new ShippingTriggeredEvent();
    event.setId(randomUUID());
    event.setUserId(USER_ID);
    event.setOrderId(ORDER_ID);
    event.setStatus(ShippingStatus.SUCCESS);
    return event;
  }

  private ShippingTriggerCommand getShippingTriggerCommand() {
    final ShippingTriggerCommand command = new ShippingTriggerCommand();
    command.setId(randomUUID());
    command.setUserId(USER_ID);
    command.setOrderId(ORDER_ID);
    command.setAmount(33.0);
    command.setProducts(List.of(getProductEvent()));
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
    event.setDiscount(1);
    return event;
  }
}
