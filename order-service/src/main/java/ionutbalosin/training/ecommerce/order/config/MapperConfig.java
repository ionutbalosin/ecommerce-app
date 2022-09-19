package ionutbalosin.training.ecommerce.order.config;

import ionutbalosin.training.ecommerce.order.dto.mapper.OrderDtoMapper;
import ionutbalosin.training.ecommerce.order.dto.mapper.OrderMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

  @Bean
  public OrderMapper orderMapper() {
    return new OrderMapper();
  }

  @Bean
  public OrderDtoMapper orderDtoMapper() {
    return new OrderDtoMapper();
  }
}
