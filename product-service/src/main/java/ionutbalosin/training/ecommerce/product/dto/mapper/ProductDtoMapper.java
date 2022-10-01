package ionutbalosin.training.ecommerce.product.dto.mapper;

import static ionutbalosin.training.ecommerce.product.api.model.ProductDto.CurrencyEnum.fromValue;

import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import ionutbalosin.training.ecommerce.product.api.model.ProductIdDto;
import ionutbalosin.training.ecommerce.product.model.Product;
import java.util.UUID;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
public class ProductDtoMapper {

  public ProductDto map(Product product) {
    return new ProductDto()
        .productId(product.getId())
        .name(product.getName())
        .brand(product.getBrand())
        .category(product.getCategory())
        .price(product.getPrice())
        .currency(fromValue(product.getCurrency()))
        .quantity(product.getQuantity());
  }

  public ProductIdDto map(UUID uuid) {
    return new ProductIdDto().productId(uuid);
  }
}
