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
package ionutbalosin.training.ecommerce.shipping.client;

import ionutbalosin.training.ecommerce.payment.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.List.of;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.GATEWAY_TIMEOUT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

/*
 * The payment client simulates both failures but also success scenarios using an external
 * service @See https://httpstat.us
 */
@Component
public class ShppingClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentClient.class);

  private final List<Integer> HTTP_CODES =
      of(
          OK.value(),
          CREATED.value(),
          ACCEPTED.value(),
          BAD_REQUEST.value(),
          FORBIDDEN.value(),
          SERVICE_UNAVAILABLE.value(),
          GATEWAY_TIMEOUT.value());

  private final RestTemplate restTemplate;

  public ShppingClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public HttpStatusCode pay(Payment payment) {
    final Integer httpCodeSimulator = HTTP_CODES.get(current().nextInt(HTTP_CODES.size()));
    final String serviceUrl = "https://httpstat.us/" + httpCodeSimulator;

    LOGGER.debug(
        "Trigger payment for user id '{}', order id '{}', and server url '{}')",
        payment.getUserId(),
        payment.getOrderId(),
        serviceUrl);

    final ResponseEntity responseEntity = restTemplate.getForEntity(serviceUrl, Object.class);
    final HttpStatusCode statusCode = responseEntity.getStatusCode();

    return statusCode;
  }
}
