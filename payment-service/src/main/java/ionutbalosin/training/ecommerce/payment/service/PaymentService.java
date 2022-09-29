package ionutbalosin.training.ecommerce.payment.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus;
import ionutbalosin.training.ecommerce.payment.client.PaymentClient;
import ionutbalosin.training.ecommerce.payment.dto.mapper.PaymentStatusMapper;
import ionutbalosin.training.ecommerce.payment.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
@Service
public class PaymentService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

  public static final String CIRCUIT_BREAKER_NAME = "paymentCircuitBreaker";

  private final PaymentClient paymentClient;
  private final PaymentStatusMapper paymentStatusMapper;

  public PaymentService(PaymentClient paymentClient, PaymentStatusMapper paymentStatusMapper) {
    this.paymentClient = paymentClient;
    this.paymentStatusMapper = paymentStatusMapper;
  }

  @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "fallback")
  public PaymentStatus triggerPayment(Payment payment) {
    final HttpStatus httpStatus = paymentClient.pay(payment);
    final PaymentStatus paymentStatus = paymentStatusMapper.map(httpStatus);

    LOGGER.debug(
        "Payment for user id '{}', and order id '{}' received status '{}')",
        payment.getUserId(),
        payment.getOrderId(),
        paymentStatus);

    return paymentStatus;
  }

  public PaymentStatus fallback(Exception e) {
    LOGGER.error("Payment fallback method. Error encountered = '{}'", e.getMessage());
    return PaymentStatus.FAILED;
  }
}
