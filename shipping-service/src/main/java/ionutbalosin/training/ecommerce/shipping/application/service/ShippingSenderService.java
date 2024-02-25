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
package ionutbalosin.training.ecommerce.shipping.application.service;

import static ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus.IN_PROGRESS;
import static java.time.Instant.now;

import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatusUpdatedEvent;
import ionutbalosin.training.ecommerce.shipping.application.event.builder.ShippingEventBuilder;
import ionutbalosin.training.ecommerce.shipping.domain.model.Shipping;
import ionutbalosin.training.ecommerce.shipping.domain.port.ShippingSenderPort;
import ionutbalosin.training.ecommerce.shipping.domain.service.ShippingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class ShippingSenderService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShippingSenderService.class);

  private final ShippingEventBuilder shippingEventBuilder;
  private final ShippingSenderPort shippingSenderPort;
  private final ShippingService shippingService;
  private final ThreadPoolTaskScheduler taskScheduler;

  @Value("${shipping.delayInSec}")
  private long shippingDelay;

  public ShippingSenderService(
      ShippingEventBuilder shippingEventBuilder,
      ShippingSenderPort shippingSenderPort,
      ShippingService shippingService,
      ThreadPoolTaskScheduler taskScheduler) {
    this.shippingEventBuilder = shippingEventBuilder;
    this.shippingSenderPort = shippingSenderPort;
    this.shippingService = shippingService;
    this.taskScheduler = taskScheduler;
  }

  public ShippingStatus triggerShipping(Shipping shipping) {
    LOGGER.debug(
        "Trigger shipping for user id '{}', and order id '{}')",
        shipping.getUserId(),
        shipping.getOrderId());
    taskScheduler.schedule(() -> ship(shipping), now().plusSeconds(shippingDelay));

    // Assume IN_PROGRESS is a proper status indicating the shipping process has started
    return IN_PROGRESS;
  }

  private void ship(Shipping shipping) {
    final ShippingStatus shippingStatus = shippingService.ship(shipping);
    LOGGER.debug(
        "Shipping for user id '{}', and order id '{}' received status '{}'",
        shipping.getUserId(),
        shipping.getOrderId(),
        shippingStatus);

    final ShippingStatusUpdatedEvent event =
        shippingEventBuilder.createEvent(shipping, shippingStatus);
    shippingSenderPort.send(event);
  }
}
