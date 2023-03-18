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

import static ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus.APPROVED;
import static ionutbalosin.training.ecommerce.order.listener.PaymentEventListener.PAYMENTS_OUT_TOPIC;
import static ionutbalosin.training.ecommerce.order.model.OrderStatus.PAYMENT_APPROVED;
import static ionutbalosin.training.ecommerce.order.model.OrderStatus.PAYMENT_FAILED;
import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import ionutbalosin.training.ecommerce.message.schema.payment.PaymentTriggeredEvent;
import ionutbalosin.training.ecommerce.order.KafkaContainerConfiguration;
import ionutbalosin.training.ecommerce.order.KafkaSingletonContainer;
import ionutbalosin.training.ecommerce.order.PostgresqlSingletonContainer;
import ionutbalosin.training.ecommerce.order.model.Order;
import ionutbalosin.training.ecommerce.order.service.OrderService;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest()
@Import(KafkaContainerConfiguration.class)
public class PaymentEventListenerTest {

  private final UUID PREFILLED_USER_ID = fromString("42424242-4242-4242-4242-424242424242");
  private final UUID PREFILLED_ORDER_ID = fromString("307e0ab9-3900-11ed-a261-0242ac120002");

  private final PaymentTriggeredEvent PAYMENT_TRIGGERED = getPaymentTriggeredEvent();

  @Container
  private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER =
      PostgresqlSingletonContainer.INSTANCE.getContainer();

  @Container
  private static final KafkaContainer KAFKA_CONTAINER =
      KafkaSingletonContainer.INSTANCE.getContainer();

  @Autowired private PaymentEventListener classUnderTest;
  @Autowired private KafkaTemplate<String, PaymentTriggeredEvent> kafkaTemplate;
  @Autowired private OrderService orderService;

  @Test
  public void consumeTest_prefilledData() {
    // this test relies on the prefilled DB data
    final Order initialOrder = orderService.getOrder(PAYMENT_TRIGGERED.getOrderId());
    assertEquals(PAYMENT_FAILED, initialOrder.getStatus());

    kafkaTemplate.send(PAYMENTS_OUT_TOPIC, PAYMENT_TRIGGERED);

    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              final Order updatedOrder = orderService.getOrder(PAYMENT_TRIGGERED.getOrderId());
              if (updatedOrder.getStatus() != PAYMENT_APPROVED) {
                return false;
              }

              assertEquals(PAYMENT_APPROVED, updatedOrder.getStatus());
              return true;
            });
  }

  private PaymentTriggeredEvent getPaymentTriggeredEvent() {
    final PaymentTriggeredEvent event = new PaymentTriggeredEvent();
    event.setId(randomUUID());
    event.setUserId(PREFILLED_USER_ID);
    event.setOrderId(PREFILLED_ORDER_ID);
    event.setStatus(APPROVED);
    return event;
  }
}
