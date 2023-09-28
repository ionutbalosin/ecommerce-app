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

import static io.github.resilience4j.circuitbreaker.CircuitBreaker.State.CLOSED;
import static io.github.resilience4j.circuitbreaker.CircuitBreaker.State.OPEN;
import static ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus.FAILED;
import static ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus.SUCCESS;
import static ionutbalosin.training.ecommerce.shipping.service.ShippingService.CIRCUIT_BREAKER_NAME;
import static java.util.UUID.fromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus;
import ionutbalosin.training.ecommerce.shipping.model.Product;
import ionutbalosin.training.ecommerce.shipping.model.Shipping;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest()
public class ShippingServiceTest {

  private final UUID USER_ID = fromString("fdc888dc-39ba-11ed-a261-0242ac120002");
  private final UUID ORDER_ID = fromString("fdc881e8-39ba-11ed-a261-0242ac120002");

  @Autowired private ShippingService classUnderTest;
  @Autowired protected CircuitBreakerRegistry circuitBreakerRegistry;
  @MockBean private RestTemplate restTemplate;

  @Value("${resilience4j.circuitbreaker.instances.shippingCircuitBreaker.sliding-window-size}")
  int slidingWindowSize;

  final Product EVENT =
      new Product()
          .productId(fromString("275c59ca-5dce-11ee-8c99-0242ac120002"))
          .name("Peet's Coffee")
          .brand("Colombia Luminosa (Light Roast)")
          .price(11)
          .currency(Product.CurrencyEnum.EUR)
          .quantity(111);

  final Shipping SHIPPING =
      new Shipping()
          .userId(USER_ID)
          .orderId(ORDER_ID)
          .amount(33.33f)
          .currency(Shipping.ShippingCurrency.EUR)
          .products(List.of(EVENT));

  @BeforeEach
  public void setUp() {
    circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).reset();
  }

  @Test
  public void triggerShipping_serviceAvailable_circuitBreakerClosed() {
    when(restTemplate.getForEntity(anyString(), any()))
        .thenReturn(new ResponseEntity(HttpStatus.OK));

    final ShippingStatus result = classUnderTest.triggerShipping(SHIPPING);
    assertEquals(SUCCESS, result);
  }

  @Test
  public void triggerShipping_serviceAvailable_circuitBreakerOpen() {
    final CircuitBreaker circuitBreaker =
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME);

    when(restTemplate.getForEntity(anyString(), any()))
        .thenReturn(new ResponseEntity(HttpStatus.OK));

    circuitBreaker.transitionToOpenState();

    final ShippingStatus result = classUnderTest.triggerShipping(SHIPPING);
    assertEquals(FAILED, result);
  }

  @Test
  public void triggerShipping_serviceUnavailable() {
    final CircuitBreaker circuitBreaker =
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME);

    when(restTemplate.getForEntity(anyString(), any()))
        .thenThrow(new ResponseStatusException(SERVICE_UNAVAILABLE));

    assertEquals(CLOSED, circuitBreaker.getState());

    for (int retry = 0; retry < slidingWindowSize + 1; retry++) {
      final ShippingStatus result = classUnderTest.triggerShipping(SHIPPING);
      assertEquals(FAILED, result);
    }

    assertEquals(OPEN, circuitBreaker.getState());
  }
}
