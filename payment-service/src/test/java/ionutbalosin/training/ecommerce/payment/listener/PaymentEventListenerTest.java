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
package ionutbalosin.training.ecommerce.payment.listener;

import static ionutbalosin.training.ecommerce.payment.KafkaContainerConfiguration.consumerConfigs;
import static ionutbalosin.training.ecommerce.payment.listener.PaymentEventListener.PAYMENTS_IN_TOPIC;
import static ionutbalosin.training.ecommerce.payment.listener.PaymentEventListener.PAYMENTS_OUT_TOPIC;
import static java.util.List.of;
import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import ionutbalosin.training.ecommerce.message.schema.currency.Currency;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatusUpdatedEvent;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentTriggerCommand;
import ionutbalosin.training.ecommerce.payment.KafkaContainerConfiguration;
import ionutbalosin.training.ecommerce.payment.KafkaSingletonContainer;
import java.time.Duration;
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
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest()
@Import(KafkaContainerConfiguration.class)
public class PaymentEventListenerTest {

  private final UUID USER_ID = fromString("fdc888dc-39ba-11ed-a261-0242ac120002");
  private final UUID ORDER_ID = fromString("fdc881e8-39ba-11ed-a261-0242ac120002");
  private final PaymentTriggerCommand PAYMENT_TRIGGER = getPaymentTriggerCommandCommand();
  private final PaymentStatusUpdatedEvent PAYMENT_STATUS_UPDATE = getPaymentStatusUpdatedEvent();

  @Container
  private static final KafkaContainer KAFKA_CONTAINER =
      KafkaSingletonContainer.INSTANCE.getContainer();

  @Autowired private PaymentEventListener classUnderTest;
  @Autowired private KafkaTemplate<String, PaymentTriggerCommand> kafkaTemplate;

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
    final KafkaConsumer<String, PaymentStatusUpdatedEvent> kafkaConsumer =
        new KafkaConsumer(consumerConfigs());
    kafkaConsumer.subscribe(of(PAYMENTS_OUT_TOPIC));

    kafkaTemplate.send(PAYMENTS_IN_TOPIC, PAYMENT_TRIGGER);

    await()
        .atMost(20, TimeUnit.SECONDS)
        .until(
            () -> {
              final ConsumerRecords<String, PaymentStatusUpdatedEvent> records =
                  kafkaConsumer.poll(Duration.ofMillis(500));
              if (records.isEmpty()) {
                return false;
              }

              assertEquals(1, records.count());
              records.forEach(
                  record -> {
                    assertNotNull(record.value().getId());
                    assertNotNull(record.value().getStatus());
                    assertEquals(PAYMENT_STATUS_UPDATE.getUserId(), record.value().getUserId());
                    assertEquals(PAYMENT_STATUS_UPDATE.getOrderId(), record.value().getOrderId());
                  });
              return true;
            });

    kafkaConsumer.unsubscribe();
    kafkaConsumer.close();
  }

  private PaymentStatusUpdatedEvent getPaymentStatusUpdatedEvent() {
    final PaymentStatusUpdatedEvent event = new PaymentStatusUpdatedEvent();
    event.setId(randomUUID());
    event.setUserId(USER_ID);
    event.setOrderId(ORDER_ID);
    event.setStatus(PaymentStatus.APPROVED);
    return event;
  }

  private PaymentTriggerCommand getPaymentTriggerCommandCommand() {
    final PaymentTriggerCommand command = new PaymentTriggerCommand();
    command.setId(randomUUID());
    command.setUserId(USER_ID);
    command.setOrderId(ORDER_ID);
    command.setAmount(33.0);
    command.setCurrency(Currency.EUR);
    return command;
  }
}
