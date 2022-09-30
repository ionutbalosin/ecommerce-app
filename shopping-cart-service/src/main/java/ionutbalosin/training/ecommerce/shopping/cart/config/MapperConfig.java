package ionutbalosin.training.ecommerce.shopping.cart.config;

import ionutbalosin.training.ecommerce.shopping.cart.dao.mapper.CartItemRowMapper;
import ionutbalosin.training.ecommerce.shopping.cart.dto.mapper.CartItemDtoMapper;
import ionutbalosin.training.ecommerce.shopping.cart.dto.mapper.ProductEventMapper;
import ionutbalosin.training.ecommerce.shopping.cart.model.mapper.CartItemMapper;
import ionutbalosin.training.ecommerce.shopping.cart.model.mapper.ProductItemMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
@Configuration
public class MapperConfig {

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

  @Bean
  public ProductEventMapper productEventMapper() {
    return new ProductEventMapper();
  }

  @Bean
  public ProductItemMapper productItemMapper() {
    return new ProductItemMapper();
  }
}
