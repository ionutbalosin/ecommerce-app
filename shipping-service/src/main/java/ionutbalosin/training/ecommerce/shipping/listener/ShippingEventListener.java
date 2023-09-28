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

import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingTriggerCommand;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingTriggeredEvent;
import ionutbalosin.training.ecommerce.shipping.event.builder.ShippingEventBuilder;
import ionutbalosin.training.ecommerce.shipping.model.Shipping;
import ionutbalosin.training.ecommerce.shipping.model.mapper.ShippingMapper;
import ionutbalosin.training.ecommerce.shipping.service.ShippingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
public class ShippingEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShippingEventListener.class);

  public static final String SHIPPING_IN_TOPIC = "ecommerce-shipping-in-topic";
  public static final String SHIPPING_OUT_TOPIC = "ecommerce-shipping-out-topic";

  private final ShippingMapper shippingMapper;
  private final ShippingService shippingService;
  private final ShippingEventBuilder shippingEventBuilder;

  public ShippingEventListener(
      ShippingMapper shippingMapper,
      ShippingService shippingService,
      ShippingEventBuilder shippingEventBuilder) {
    this.shippingMapper = shippingMapper;
    this.shippingService = shippingService;
    this.shippingEventBuilder = shippingEventBuilder;
  }

  @KafkaListener(topics = SHIPPING_IN_TOPIC, groupId = "ecommerce_group_id")
  @SendTo(SHIPPING_OUT_TOPIC)
  public ShippingTriggeredEvent receive(ShippingTriggerCommand shippingCommand) {
    LOGGER.debug("Received message '{}' from Kafka topic '{}'", shippingCommand, SHIPPING_IN_TOPIC);
    final Shipping shipping = shippingMapper.map(shippingCommand);
    final ShippingStatus shippingStatus = shippingService.triggerShipping(shipping);
    final ShippingTriggeredEvent shippingEvent =
        shippingEventBuilder.createEvent(shipping, shippingStatus);
    LOGGER.debug("Produce message '{}' to Kafka topic '{}'", shippingEvent, SHIPPING_OUT_TOPIC);
    return shippingEvent;
  }
}