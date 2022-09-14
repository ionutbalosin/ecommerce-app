package ionutbalosin.training.ecommerce.shopping.cart.config;

import ionutbalosin.training.ecommerce.shopping.cart.dao.mapper.CartItemRowMapper;
import ionutbalosin.training.ecommerce.shopping.cart.dto.mapper.CartItemDtoMapper;
import ionutbalosin.training.ecommerce.shopping.cart.model.mapper.CartItemMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {

  @Bean
  public CartItemDtoMapper cartItemDtoMapper() {
    return new CartItemDtoMapper();
  }

  @Bean
  public CartItemMapper cartItemMapper() {
    return new CartItemMapper();
  }

  @Bean
  public CartItemRowMapper productRowMapper() {
    return new CartItemRowMapper();
  }
}
