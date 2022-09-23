package ionutbalosin.training.ecommerce.payment.service;

import static java.util.List.of;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.GATEWAY_TIMEOUT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus;
import ionutbalosin.training.ecommerce.payment.dto.mapper.PaymentStatusMapper;
import ionutbalosin.training.ecommerce.payment.model.Payment;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
//
// The payment service simulates both failures but also success scenarios using an external
// service @See https://httpstat.us
//
@Service
public class PaymentService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);
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
  private final PaymentStatusMapper paymentStatusMapper;

  public PaymentService(RestTemplate restTemplate, PaymentStatusMapper paymentStatusMapper) {
    this.restTemplate = restTemplate;
    this.paymentStatusMapper = paymentStatusMapper;
  }

  @CircuitBreaker(name = "paymentCircuitBreaker", fallbackMethod = "fallback")
  public PaymentStatus triggerPayment(Payment payment) {

    final Integer httpCodeSimulator = HTTP_CODES.get(current().nextInt(HTTP_CODES.size()));
    final String serviceUrl = "https://httpstat.us/" + httpCodeSimulator;
    final ResponseEntity responseEntity = restTemplate.getForEntity(serviceUrl, Object.class);
    final HttpStatus httpStatus = responseEntity.getStatusCode();
    final PaymentStatus paymentStatus = paymentStatusMapper.map(httpStatus);

    LOGGER.debug(
        "Payment for user '{}', order id = '{}', and amount = '{}' received status '{}' (service"
            + " url = '{}')",
        payment.getUserId(),
        payment.getOrderId(),
        payment.getAmount(),
        httpStatus,
        serviceUrl);

    return paymentStatus;
  }

  public PaymentStatus fallback(CallNotPermittedException e) {
    LOGGER.error("Cannot proceed with the payment. Error encountered = '{}'", e);
    return PaymentStatus.FAILED;
  }
}
