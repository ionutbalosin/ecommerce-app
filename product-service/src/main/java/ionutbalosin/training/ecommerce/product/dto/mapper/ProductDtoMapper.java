package ionutbalosin.training.ecommerce.product.dto.mapper;

import static ionutbalosin.training.ecommerce.product.api.model.ProductDto.CurrencyEnum.fromValue;
import static java.math.BigDecimal.valueOf;

import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import ionutbalosin.training.ecommerce.product.api.model.ProductIdDto;
import ionutbalosin.training.ecommerce.product.model.Product;
import java.util.UUID;

public class ProductDtoMapper {

  public ProductDto map(Product product) {
    return new ProductDto()
        .productId(product.getId())
        .name(product.getName())
        .brand(product.getBrand())
        .category(product.getCategory())
        .price(valueOf(product.getPrice()))
        .currency(fromValue(product.getCurrency()))
        .quantity(product.getQuantity());
  }

  public ProductIdDto map(UUID uuid) {
    return new ProductIdDto().productId(uuid);
  }
}
