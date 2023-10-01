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
package ionutbalosin.training.ecommerce.shipping.service;

import static ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus.COMPLETED;
import static ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus.FAILED;
import static java.time.Instant.now;

import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatusUpdatedEvent;
import ionutbalosin.training.ecommerce.shipping.event.builder.ShippingEventBuilder;
import ionutbalosin.training.ecommerce.shipping.model.Shipping;
import ionutbalosin.training.ecommerce.shipping.sender.ShippingEventSender;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

/*
 * Simulates both failures but also completed shipments
 */
@Service
public class ShippingSimulator {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShippingSimulator.class);
  private static final Random RANDOM = new Random(16384);

  private final ShippingEventBuilder shippingEventBuilder;
  private final ShippingEventSender shippingEventSender;
  private final ThreadPoolTaskScheduler taskScheduler;

  @Value("${shipping.delayInSec}")
  private long shippingDelay;

  public ShippingSimulator(
      ShippingEventBuilder shippingEventBuilder,
      ShippingEventSender shippingEventSender,
      ThreadPoolTaskScheduler taskScheduler) {
    this.shippingEventSender = shippingEventSender;
    this.shippingEventBuilder = shippingEventBuilder;
    this.taskScheduler = taskScheduler;
  }

  // Simulate (i.e., schedule) shipping at a later time
  public void scheduleShipping(Shipping shipping) {
    taskScheduler.schedule(() -> ship(shipping), now().plusSeconds(shippingDelay));
  }

  private void ship(Shipping shipping) {
    final ShippingStatus shippingStatusSimulator = (RANDOM.nextBoolean()) ? COMPLETED : FAILED;
    final ShippingStatusUpdatedEvent event =
        shippingEventBuilder.createEvent(shipping, shippingStatusSimulator);

    LOGGER.debug(
        "Shipping for user id '{}', and order id '{}' received status '{}'",
        shipping.getUserId(),
        shipping.getOrderId(),
        shippingStatusSimulator);
    shippingEventSender.send(event);
  }
}
