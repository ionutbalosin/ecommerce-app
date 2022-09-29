package ionutbalosin.training.ecommerce.payment.service;

import static ionutbalosin.training.ecommerce.payment.KafkaContainerConfiguration.consumerConfigs;
import static ionutbalosin.training.ecommerce.payment.service.PaymentEventListener.PAYMENTS_IN_TOPIC;
import static ionutbalosin.training.ecommerce.payment.service.PaymentEventListener.PAYMENTS_OUT_TOPIC;
import static java.util.List.of;
import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import ionutbalosin.training.ecommerce.message.schema.payment.PaymentCurrency;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentTriggeredEvent;
import ionutbalosin.training.ecommerce.message.schema.payment.TriggerPaymentCommand;
import ionutbalosin.training.ecommerce.payment.KafkaContainerConfiguration;
import ionutbalosin.training.ecommerce.payment.KafkaSingletonContainer;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
@ExtendWith(SpringExtension.class)
@Import(KafkaContainerConfiguration.class)
@SpringBootTest()
public class PaymentEventListenerTest {

  private final UUID USER_ID = fromString("fdc888dc-39ba-11ed-a261-0242ac120002");
  private final UUID ORDER_ID = fromString("fdc881e8-39ba-11ed-a261-0242ac120002");
  private final TriggerPaymentCommand TRIGGER_PAYMENT = getTriggerPaymentCommand();
  private final PaymentTriggeredEvent PAYMENT_TRIGGERED = getPaymentTriggeredEvent();

  @Container
  private static final KafkaContainer KAFKA_CONTAINER =
      KafkaSingletonContainer.INSTANCE.getContainer();

  @Autowired private PaymentEventListener classUnderTest;
  @Autowired private KafkaTemplate<String, TriggerPaymentCommand> kafkaTemplate;

  @Test
  public void consumeTest() {
    final KafkaConsumer<String, PaymentTriggeredEvent> kafkaConsumer =
        new KafkaConsumer(consumerConfigs());
    kafkaConsumer.subscribe(of(PAYMENTS_OUT_TOPIC));

    kafkaTemplate.send(PAYMENTS_IN_TOPIC, TRIGGER_PAYMENT);

    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              final ConsumerRecords<String, PaymentTriggeredEvent> records =
                  kafkaConsumer.poll(Duration.ofMillis(500));
              if (records.isEmpty()) {
                return false;
              }

              assertEquals(1, records.count());
              records.forEach(
                  record -> {
                    assertNotNull(record.value().getId());
                    assertNotNull(record.value().getStatus());
                    assertEquals(PAYMENT_TRIGGERED.getUserId(), record.value().getUserId());
                    assertEquals(PAYMENT_TRIGGERED.getOrderId(), record.value().getOrderId());
                  });
              return true;
            });
  }

  private PaymentTriggeredEvent getPaymentTriggeredEvent() {
    final PaymentTriggeredEvent event = new PaymentTriggeredEvent();
    event.setId(randomUUID());
    event.setUserId(USER_ID);
    event.setOrderId(ORDER_ID);
    event.setStatus(PaymentStatus.APPROVED);
    return event;
  }

  private TriggerPaymentCommand getTriggerPaymentCommand() {
    final TriggerPaymentCommand command = new TriggerPaymentCommand();
    command.setId(randomUUID());
    command.setUserId(USER_ID);
    command.setOrderId(ORDER_ID);
    command.setDescription("Payment for user id " + command.getUserId());
    command.setAmount(33);
    command.setCurrency(PaymentCurrency.EUR);
    return command;
  }
}
