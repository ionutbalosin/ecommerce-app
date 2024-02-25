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
package ionutbalosin.training.ecommerce.shipping.adapter.sender;

import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatusUpdatedEvent;
import ionutbalosin.training.ecommerce.shipping.adapter.sender.event.builder.ShippingEventSenderBuilder;
import ionutbalosin.training.ecommerce.shipping.domain.model.Shipping;
import ionutbalosin.training.ecommerce.shipping.domain.port.ShippingEventSenderPort;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
public class ShippingEventSenderAdapter implements ShippingEventSenderPort {

  public static final String SHIPPING_OUT_TOPIC = "ecommerce-shipping-out-topic";
  private static final Logger LOGGER = LoggerFactory.getLogger(ShippingEventSenderAdapter.class);

  private final ShippingEventSenderBuilder shippingEventSenderBuilder;
  private final KafkaTemplate<String, ShippingStatusUpdatedEvent> kafkaTemplate;

  public ShippingEventSenderAdapter(
      ShippingEventSenderBuilder shippingEventSenderBuilder,
      KafkaTemplate<String, ShippingStatusUpdatedEvent> kafkaTemplate) {
    this.shippingEventSenderBuilder = shippingEventSenderBuilder;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void send(Shipping shipping, ShippingStatus shippingStatus) {
    final ShippingStatusUpdatedEvent event =
        shippingEventSenderBuilder.createEvent(shipping, shippingStatus);

    final CompletableFuture<SendResult<String, ShippingStatusUpdatedEvent>> future =
        kafkaTemplate.send(SHIPPING_OUT_TOPIC, event);

    future.whenComplete(
        (result, failure) -> {
          if (failure == null) {
            LOGGER.debug("Sent message '{}' to Kafka topic '{}'", event, SHIPPING_OUT_TOPIC);
          } else {
            LOGGER.error(
                "Unable to send message '{}' to Kafka topic '{}', exception '{}'",
                event,
                SHIPPING_OUT_TOPIC,
                failure);
          }
        });
  }
}
