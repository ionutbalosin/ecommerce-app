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
package ionutbalosin.training.ecommerce.notification.listener;

import static ionutbalosin.training.ecommerce.notification.listener.NotificationEventListener.NOTIFICATIONS_TOPIC;
import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import ionutbalosin.training.ecommerce.message.schema.currency.Currency;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatusUpdatedEvent;
import ionutbalosin.training.ecommerce.message.schema.product.ProductEvent;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatusUpdatedEvent;
import ionutbalosin.training.ecommerce.notification.KafkaContainerConfiguration;
import ionutbalosin.training.ecommerce.notification.KafkaSingletonContainer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest()
@Import(KafkaContainerConfiguration.class)
public class NotificationEventListenerTest {

  private final UUID USER_ID = fromString("fdc888dc-39ba-11ed-a261-0242ac120002");
  private final UUID ORDER_ID = fromString("fdc881e8-39ba-11ed-a261-0242ac120002");
  private final ProductEvent PRODUCT_EVENT = getProductEvent();
  private final PaymentStatusUpdatedEvent PAYMENT_STATUS_UPDATE = getPaymentStatusUpdatedEvent();
  private final ShippingStatusUpdatedEvent SHIPPING_STATUS_UPDATE = getShippingStatusUpdatedEvent();

  @Container
  private static final KafkaContainer KAFKA_CONTAINER =
      KafkaSingletonContainer.INSTANCE.getContainer();

  @Autowired private NotificationEventListener classUnderTest;
  @Autowired private KafkaTemplate<String, PaymentStatusUpdatedEvent> kafkaTemplate1;
  @Autowired private KafkaTemplate<String, ShippingStatusUpdatedEvent> kafkaTemplate2;

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
  public void send_paymentStatusUpdatedEvent() {
    CompletableFuture<SendResult<String, PaymentStatusUpdatedEvent>> completableFuture =
        kafkaTemplate1.send(NOTIFICATIONS_TOPIC, PAYMENT_STATUS_UPDATE);

    await().atMost(20, TimeUnit.SECONDS).until(() -> completableFuture.isDone());

    assertTrue(completableFuture.isDone());
    assertFalse(completableFuture.isCompletedExceptionally());
  }

  @Test
  @DirtiesContext
  public void send_shippingStatusUpdatedEvent() {
    CompletableFuture<SendResult<String, ShippingStatusUpdatedEvent>> completableFuture =
        kafkaTemplate2.send(NOTIFICATIONS_TOPIC, SHIPPING_STATUS_UPDATE);

    await().atMost(20, TimeUnit.SECONDS).until(() -> completableFuture.isDone());

    assertTrue(completableFuture.isDone());
    assertFalse(completableFuture.isCompletedExceptionally());
  }

  public PaymentStatusUpdatedEvent getPaymentStatusUpdatedEvent() {
    final PaymentStatusUpdatedEvent event = new PaymentStatusUpdatedEvent();
    event.setId(randomUUID());
    event.setOrderId(ORDER_ID);
    event.setUserId(USER_ID);
    event.setStatus(PaymentStatus.APPROVED);
    return event;
  }

  public ShippingStatusUpdatedEvent getShippingStatusUpdatedEvent() {
    final ShippingStatusUpdatedEvent event = new ShippingStatusUpdatedEvent();
    event.setId(randomUUID());
    event.setOrderId(ORDER_ID);
    event.setUserId(USER_ID);
    event.setStatus(ShippingStatus.IN_PROGRESS);
    return event;
  }

  private ProductEvent getProductEvent() {
    final ProductEvent event = new ProductEvent();
    event.setProductId(fromString("46414ebe-5dcd-11ee-8c99-0242ac120002"));
    event.setName("Illy");
    event.setBrand("Ground Coffee: illy Medium Roast Ground Coffee");
    event.setPrice(11);
    event.setCurrency(Currency.EUR);
    event.setQuantity(111);
    event.setDiscount(1);
    return event;
  }
}
