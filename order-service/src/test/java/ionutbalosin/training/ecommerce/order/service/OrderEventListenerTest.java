package ionutbalosin.training.ecommerce.order.service;

import static ionutbalosin.training.ecommerce.order.KafkaContainerConfiguration.consumerConfigs;
import static java.util.List.of;
import static java.util.UUID.fromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import ionutbalosin.training.ecommerce.event.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.event.schema.order.OrderCurrencyEnumEvent;
import ionutbalosin.training.ecommerce.event.schema.order.ProductEvent;
import ionutbalosin.training.ecommerce.event.schema.payment.PaymentCurrencyEnumEvent;
import ionutbalosin.training.ecommerce.event.schema.payment.PaymentInitiatedEvent;
import ionutbalosin.training.ecommerce.order.KafkaContainerConfiguration;
import ionutbalosin.training.ecommerce.order.KafkaSingletonContainer;
import ionutbalosin.training.ecommerce.order.PostgresqlSingletonContainer;
import java.time.Duration;
import java.util.List;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@ExtendWith(SpringExtension.class)
@Import(KafkaContainerConfiguration.class)
@SpringBootTest()
public class OrderEventListenerTest {

  final ProductEvent PRODUCT_EVENT = getProductEvent();
  final OrderCreatedEvent ORDER_CREATED = getOrderCreatedEvent();

  @Container
  private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER =
      PostgresqlSingletonContainer.INSTANCE.getContainer();

  @Container
  private static final KafkaContainer KAFKA_CONTAINER =
      KafkaSingletonContainer.INSTANCE.getContainer();

  @Autowired private OrderEventListener orderEventListener;
  @Autowired private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

  @Test
  public void consumeTest() {
    final KafkaConsumer<String, PaymentInitiatedEvent> kafkaConsumer =
        new KafkaConsumer(consumerConfigs());
    kafkaConsumer.subscribe(of("ecommerce-payments-topic"));

    kafkaTemplate.send("ecommerce-orders-topic", ORDER_CREATED);

    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              final ConsumerRecords<String, PaymentInitiatedEvent> records =
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
                    assertEquals(
                        "Payment for user id " + record.value().getUserId(),
                        record.value().getDescription());
                    assertEquals(PaymentCurrencyEnumEvent.EUR, record.value().getCurrency());
                  });
              return true;
            });
  }

  private ProductEvent getProductEvent() {
    final ProductEvent productEvent = new ProductEvent();
    productEvent.setProductId(fromString("02f85436-397f-11ed-a261-0242ac120002"));
    productEvent.setName("Präsident Ganze Bohne");
    productEvent.setBrand("Julius Meinl");
    productEvent.setPrice(11);
    productEvent.setCurrency(OrderCurrencyEnumEvent.EUR);
    productEvent.setQuantity(111);
    productEvent.setDiscount(1);
    return productEvent;
  }

  private OrderCreatedEvent getOrderCreatedEvent() {
    final OrderCreatedEvent orderCreated = new OrderCreatedEvent();
    orderCreated.setId(fromString("0b9b15a6-397f-11ed-a261-0242ac120002"));
    orderCreated.setUserId(fromString("42424242-4242-4242-4242-424242424242"));
    orderCreated.setProducts(List.of(PRODUCT_EVENT));
    orderCreated.setCurrency(OrderCurrencyEnumEvent.EUR);
    orderCreated.setAmount(22);
    return orderCreated;
  }
}
