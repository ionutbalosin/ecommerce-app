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

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus;
import ionutbalosin.training.ecommerce.shipping.client.ShippingClient;
import ionutbalosin.training.ecommerce.shipping.model.Shipping;
import ionutbalosin.training.ecommerce.shipping.model.mapper.ShippingStatusMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

@Service
public class ShippingService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShippingService.class);

  public static final String CIRCUIT_BREAKER_NAME = "shippingCircuitBreaker";

  private final ShippingClient shippingClient;
  private final ShippingStatusMapper shippingStatusMapper;

  public ShippingService(ShippingClient shippingClient, ShippingStatusMapper shippingStatusMapper) {
    this.shippingClient = shippingClient;
    this.shippingStatusMapper = shippingStatusMapper;
  }

  @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "fallback")
  public ShippingStatus triggerShipping(Shipping shipping) {
    final HttpStatusCode statusCode = shippingClient.ship(shipping);
    final ShippingStatus shippingStatus = shippingStatusMapper.map(statusCode);

    LOGGER.debug(
        "Shipping for user id '{}', and order id '{}' received status '{}')",
        shipping.getUserId(),
        shipping.getOrderId(),
        shippingStatus);

    return shippingStatus;
  }

  public ShippingStatus fallback(Exception e) {
    LOGGER.error("Shipping fallback method. Error encountered = '{}'", e.getMessage());
    return ShippingStatus.FAILED;
  }
}
