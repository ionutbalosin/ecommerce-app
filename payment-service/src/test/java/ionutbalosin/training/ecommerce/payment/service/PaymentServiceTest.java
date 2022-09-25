package ionutbalosin.training.ecommerce.payment.service;

import static io.github.resilience4j.circuitbreaker.CircuitBreaker.State.CLOSED;
import static io.github.resilience4j.circuitbreaker.CircuitBreaker.State.OPEN;
import static ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus.APPROVED;
import static ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus.FAILED;
import static ionutbalosin.training.ecommerce.payment.service.PaymentService.CIRCUIT_BREAKER_NAME;
import static java.util.UUID.fromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus;
import ionutbalosin.training.ecommerce.payment.model.Payment;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest()
public class PaymentServiceTest {

  private final UUID USER_ID = fromString("fdc888dc-39ba-11ed-a261-0242ac120002");
  private final UUID ORDER_ID = fromString("fdc881e8-39ba-11ed-a261-0242ac120002");

  @Autowired private PaymentService classUnderTest;
  @Autowired protected CircuitBreakerRegistry circuitBreakerRegistry;
  @MockBean private RestTemplate restTemplate;

  @Value("${resilience4j.circuitbreaker.instances.paymentCircuitBreaker.sliding-window-size}")
  int slidingWindowSize;

  final Payment PAYMENT =
      new Payment()
          .userId(USER_ID)
          .orderId(ORDER_ID)
          .description("Payment description")
          .amount(33.33f)
          .currency(Payment.PaymentCurrency.EUR);

  @BeforeEach
  public void setUp() {
    circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).reset();
  }

  @Test
  public void triggerPayment_serviceAvailable_circuitBreakerClosed() {
    when(restTemplate.getForEntity(anyString(), any()))
        .thenReturn(new ResponseEntity(HttpStatus.OK));

    final PaymentStatus result = classUnderTest.triggerPayment(PAYMENT);
    assertEquals(APPROVED, result);
  }

  @Test
  public void triggerPayment_serviceAvailable_circuitBreakerOpen() {
    final CircuitBreaker circuitBreaker =
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME);

    when(restTemplate.getForEntity(anyString(), any()))
        .thenReturn(new ResponseEntity(HttpStatus.OK));

    circuitBreaker.transitionToOpenState();

    final PaymentStatus result = classUnderTest.triggerPayment(PAYMENT);
    assertEquals(FAILED, result);
  }

  @Test
  public void triggerPayment_serviceUnavailable() {
    final CircuitBreaker circuitBreaker =
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME);

    when(restTemplate.getForEntity(anyString(), any()))
        .thenThrow(new ResponseStatusException(SERVICE_UNAVAILABLE));

    assertEquals(CLOSED, circuitBreaker.getState());

    for (int retry = 0; retry < slidingWindowSize + 1; retry++) {
      final PaymentStatus result = classUnderTest.triggerPayment(PAYMENT);
      assertEquals(FAILED, result);
    }

    assertEquals(OPEN, circuitBreaker.getState());
  }
}
