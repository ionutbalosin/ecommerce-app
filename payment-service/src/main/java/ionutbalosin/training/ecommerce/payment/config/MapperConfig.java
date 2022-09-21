package ionutbalosin.training.ecommerce.payment.config;

import ionutbalosin.training.ecommerce.payment.dto.mapper.PaymentMapper;
import ionutbalosin.training.ecommerce.payment.dto.mapper.PaymentStatusMapper;
import java.io.IOException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MapperConfig {

  @Bean
  public PaymentMapper paymentMapper() {
    return new PaymentMapper();
  }

  @Bean
  public PaymentStatusMapper paymentStatusMapper() {
    return new PaymentStatusMapper();
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplateBuilder().errorHandler(new RestTemplateResponseErrorHandler()).build();
  }

  class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
      return httpResponse.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) {
      // does nothing, just swallow the error
    }
  }
}
