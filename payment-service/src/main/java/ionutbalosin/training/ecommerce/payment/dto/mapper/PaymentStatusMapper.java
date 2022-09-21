package ionutbalosin.training.ecommerce.payment.dto.mapper;

import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus;
import org.springframework.http.HttpStatus;

public class PaymentStatusMapper {

  public PaymentStatus map(HttpStatus status) {
    if (status.is2xxSuccessful()) {
      return PaymentStatus.APPROVED;
    }
    return PaymentStatus.FAILED;
  }
}
