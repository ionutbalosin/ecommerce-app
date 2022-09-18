package ionutbalosin.training.ecommerce.product.config;

import ionutbalosin.training.ecommerce.product.dao.mapper.ProductRowMapper;
import ionutbalosin.training.ecommerce.product.dto.mapper.ProductDtoMapper;
import ionutbalosin.training.ecommerce.product.model.mapper.ProductMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

  @Bean
  public ProductDtoMapper productDtoMapper() {
    return new ProductDtoMapper();
  }

  @Bean
  public ProductMapper productEntityMapper() {
    return new ProductMapper();
  }

  @Bean
  public ProductRowMapper productRowMapper() {
    return new ProductRowMapper();
  }
}
