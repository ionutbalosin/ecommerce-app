package ionutbalosin.training.ecommerce.product.configuration;

import ionutbalosin.training.ecommerce.product.model.mapper.ProductDtoMapper;
import ionutbalosin.training.ecommerce.product.model.mapper.ProductEntityMapper;
import ionutbalosin.training.ecommerce.product.model.mapper.ProductRowMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {

  @Bean
  public ProductDtoMapper productDtoMapper() {
    return new ProductDtoMapper();
  }

  @Bean
  public ProductEntityMapper productEntityMapper() {
    return new ProductEntityMapper();
  }

  @Bean
  public ProductRowMapper productRowMapper() {
    return new ProductRowMapper();
  }
}
