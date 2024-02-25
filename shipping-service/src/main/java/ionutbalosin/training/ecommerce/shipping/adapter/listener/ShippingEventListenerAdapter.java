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
package ionutbalosin.training.ecommerce.shipping.adapter.listener;

import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatusUpdatedEvent;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingTriggerCommand;
import ionutbalosin.training.ecommerce.shipping.adapter.listener.event.builder.ShippingEventListenerBuilder;
import ionutbalosin.training.ecommerce.shipping.adapter.listener.mapper.ShippingMapper;
import ionutbalosin.training.ecommerce.shipping.domain.model.Shipping;
import ionutbalosin.training.ecommerce.shipping.domain.service.ShippingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class ShippingEventListenerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShippingEventListenerAdapter.class);

  public static final String SHIPPING_IN_TOPIC = "ecommerce-shipping-in-topic";
  public static final String SHIPPING_OUT_TOPIC = "ecommerce-shipping-out-topic";

  private final ShippingMapper shippingMapper;
  private final ShippingEventListenerBuilder shippingEventListenerBuilder;
  private final ShippingService shippingService;

  public ShippingEventListenerAdapter(
      ShippingMapper shippingMapper,
      ShippingEventListenerBuilder shippingEventListenerBuilder,
      ShippingService shippingService) {
    this.shippingMapper = shippingMapper;
    this.shippingEventListenerBuilder = shippingEventListenerBuilder;
    this.shippingService = shippingService;
  }

  @KafkaListener(topics = SHIPPING_IN_TOPIC, groupId = "ecommerce_group_id")
  @SendTo(SHIPPING_OUT_TOPIC)
  public ShippingStatusUpdatedEvent receive(ShippingTriggerCommand shippingCommand) {
    LOGGER.debug("Received message '{}' from Kafka topic '{}'", shippingCommand, SHIPPING_IN_TOPIC);
    final Shipping shipping = shippingMapper.map(shippingCommand);
    final ShippingStatus shippingStatus = shippingService.process(shipping);
    final ShippingStatusUpdatedEvent event =
        shippingEventListenerBuilder.createEvent(shipping, shippingStatus);
    LOGGER.debug("Produce message '{}' to Kafka topic '{}'", event, SHIPPING_OUT_TOPIC);
    return event;
  }
}
