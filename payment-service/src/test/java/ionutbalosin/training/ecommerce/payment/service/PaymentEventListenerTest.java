package ionutbalosin.training.ecommerce.payment.service;

import static ionutbalosin.training.ecommerce.payment.KafkaContainerConfiguration.consumerConfigs;
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

@ExtendWith(SpringExtension.class)
@Import(KafkaContainerConfiguration.class)
@SpringBootTest()
public class PaymentEventListenerTest {

  final TriggerPaymentCommand TRIGGER_PAYMENT = getTriggerPaymentCommand();
  final PaymentTriggeredEvent PAYMENT_TRIGGERED = getPaymentTriggeredEvent();

  @Container
  private static final KafkaContainer KAFKA_CONTAINER =
      KafkaSingletonContainer.INSTANCE.getContainer();

  @Autowired private PaymentEventListener paymentEventListener;
  @Autowired private KafkaTemplate<String, TriggerPaymentCommand> kafkaTemplate;

  @Test
  public void consumeTest() {
    final KafkaConsumer<String, PaymentTriggeredEvent> kafkaConsumer =
        new KafkaConsumer(consumerConfigs());
    kafkaConsumer.subscribe(of("ecommerce-payments-out-topic"));

    kafkaTemplate.send("ecommerce-payments-in-topic", TRIGGER_PAYMENT);

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
    event.setUserId(fromString("fdc888dc-39ba-11ed-a261-0242ac120002"));
    event.setOrderId(fromString("fdc881e8-39ba-11ed-a261-0242ac120002"));
    event.setStatus(PaymentStatus.APPROVED);
    return event;
  }

  private TriggerPaymentCommand getTriggerPaymentCommand() {
    final TriggerPaymentCommand command = new TriggerPaymentCommand();
    command.setId(randomUUID());
    command.setUserId(fromString("fdc888dc-39ba-11ed-a261-0242ac120002"));
    command.setOrderId(fromString("fdc881e8-39ba-11ed-a261-0242ac120002"));
    command.setDescription("Payment for user id " + command.getUserId());
    command.setAmount(33);
    command.setCurrency(PaymentCurrency.EUR);
    return command;
  }
}
