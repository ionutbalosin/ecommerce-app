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

import static ionutbalosin.training.ecommerce.message.schema.order.OrderStatus.PAYMENT_APPROVED;
import static ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus.APPROVED;
import static ionutbalosin.training.ecommerce.order.KafkaContainerConfiguration.consumerConfigs;
import static ionutbalosin.training.ecommerce.order.listener.PaymentEventListener.NOTIFICATIONS_TOPIC;
import static ionutbalosin.training.ecommerce.order.listener.PaymentEventListener.PAYMENTS_OUT_TOPIC;
import static ionutbalosin.training.ecommerce.order.listener.PaymentEventListener.SHIPPING_IN_TOPIC;
import static java.util.List.of;
import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import ionutbalosin.training.ecommerce.message.schema.currency.Currency;
import ionutbalosin.training.ecommerce.message.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.message.schema.order.OrderStatusUpdatedEvent;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatusUpdatedEvent;
import ionutbalosin.training.ecommerce.message.schema.product.ProductEvent;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingTriggerCommand;
import ionutbalosin.training.ecommerce.order.KafkaContainerConfiguration;
import ionutbalosin.training.ecommerce.order.KafkaSingletonContainer;
import ionutbalosin.training.ecommerce.order.PostgresqlSingletonContainer;
import ionutbalosin.training.ecommerce.order.model.Order;
import ionutbalosin.training.ecommerce.order.model.mapper.OrderMapper;
import ionutbalosin.training.ecommerce.order.service.OrderService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.avro.specific.SpecificRecordBase;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest()
@Import(KafkaContainerConfiguration.class)
public class PaymentEventListenerTest {

  private final UUID PREFILLED_USER_ID = fromString("42424242-4242-4242-4242-424242424242");

  private final ProductEvent PRODUCT_EVENT = getProductEvent();
  private final OrderCreatedEvent ORDER_CREATED = getOrderCreatedEvent();

  @Container
  private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER =
      PostgresqlSingletonContainer.INSTANCE.getContainer();

  @Container
  private static final KafkaContainer KAFKA_CONTAINER =
      KafkaSingletonContainer.INSTANCE.getContainer();

  @Autowired private PaymentEventListener classUnderTest;
  @Autowired private KafkaTemplate<String, PaymentStatusUpdatedEvent> kafkaTemplate;
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

    final PaymentStatusUpdatedEvent paymentStatusUpdatedEvent =
        getPaymentStatusUpdatedEvent(orderId);

    final KafkaConsumer<String, SpecificRecordBase> kafkaConsumer =
        new KafkaConsumer(consumerConfigs());
    kafkaConsumer.subscribe(of(NOTIFICATIONS_TOPIC, SHIPPING_IN_TOPIC));

    kafkaTemplate.send(PAYMENTS_OUT_TOPIC, paymentStatusUpdatedEvent);

    final List<ConsumerRecord<String, SpecificRecordBase>> allRecords = new ArrayList<>();
    await()
        .atMost(20, TimeUnit.SECONDS)
        .until(
            () -> {
              final ConsumerRecords<String, SpecificRecordBase> records =
                  kafkaConsumer.poll(Duration.ofMillis(500));
              if (!records.isEmpty()) {
                records.forEach(allRecords::add);
              }

              return allRecords.size() == 2;
            });

    assertEquals(2, allRecords.size());
    allRecords.forEach(
        record -> {
          if (record.value() instanceof OrderStatusUpdatedEvent) {
            assertEvent(orderId, (OrderStatusUpdatedEvent) record.value());
          }

          if (record.value() instanceof ShippingTriggerCommand) {
            assertEvent(orderId, (ShippingTriggerCommand) record.value());
          }
        });

    kafkaConsumer.unsubscribe();
    kafkaConsumer.close();
  }

  private void assertEvent(UUID orderId, OrderStatusUpdatedEvent orderEvent) {
    assertNotNull(orderEvent.getId());
    assertEquals(orderId, orderEvent.getOrderId());
    assertEquals(ORDER_CREATED.getUserId(), orderEvent.getUserId());
    assertNotNull(orderEvent.getProducts());
    assertEquals(1, orderEvent.getProducts().size());
    assertEquals(ORDER_CREATED.getAmount(), orderEvent.getAmount());
    assertEquals(ORDER_CREATED.getCurrency(), orderEvent.getCurrency());
    assertEquals(PAYMENT_APPROVED, orderEvent.getStatus());
  }

  private void assertEvent(UUID orderId, ShippingTriggerCommand shippingCommand) {
    assertNotNull(shippingCommand.getId());
    assertEquals(orderId, shippingCommand.getOrderId());
    assertEquals(ORDER_CREATED.getUserId(), shippingCommand.getUserId());
    assertEquals(ORDER_CREATED.getAmount(), shippingCommand.getAmount());
    assertEquals(Currency.EUR, shippingCommand.getCurrency());
    assertNotNull(shippingCommand.getProducts());
    assertEquals(1, shippingCommand.getProducts().size());
  }

  private PaymentStatusUpdatedEvent getPaymentStatusUpdatedEvent(UUID orderId) {
    final PaymentStatusUpdatedEvent event = new PaymentStatusUpdatedEvent();
    event.setId(randomUUID());
    event.setUserId(PREFILLED_USER_ID);
    event.setOrderId(orderId);
    event.setStatus(APPROVED);
    return event;
  }

  private ProductEvent getProductEvent() {
    final ProductEvent event = new ProductEvent();
    event.setProductId(fromString("e4bf75d0-5d22-11ee-8c99-0242ac120002"));
    event.setName("Lavazza Espresso Italiano");
    event.setBrand("Lavazza");
    event.setPrice(22);
    event.setCurrency(Currency.EUR);
    event.setQuantity(222);
    event.setDiscount(2);
    return event;
  }

  private OrderCreatedEvent getOrderCreatedEvent() {
    final OrderCreatedEvent event = new OrderCreatedEvent();
    event.setId(fromString("bcca91cc-5d22-11ee-8c99-0242ac120002"));
    event.setUserId(PREFILLED_USER_ID);
    event.setProducts(List.of(PRODUCT_EVENT));
    event.setCurrency(Currency.EUR);
    event.setAmount(33);
    return event;
  }
}
