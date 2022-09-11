package ionutbalosin.training.ecommerce.product.model.mapper;

import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import ionutbalosin.training.ecommerce.product.api.model.ProductDto.CurrencyEnum;
import ionutbalosin.training.ecommerce.product.api.model.ProductIdDto;
import ionutbalosin.training.ecommerce.product.model.entity.Product;
import java.math.BigDecimal;
import java.util.UUID;

public class ProductDtoMapper {

  public ProductDto map(Product product) {
    return new ProductDto()
        .productId(product.getId())
        .name(product.getName())
        .brand(product.getBrand())
        .category(product.getCategory())
        .price(BigDecimal.valueOf(product.getPrice()))
        .currency(CurrencyEnum.fromValue(product.getCurrency()))
        .quantity(product.getQuantity());
  }

  public ProductIdDto map(UUID uuid) {
    return new ProductIdDto().productId(uuid);
  }
}
