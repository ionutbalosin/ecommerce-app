package ionutbalosin.training.ecommerce.product.entity.mapper;

import ionutbalosin.training.ecommerce.product.api.model.ProductCreateDto;
import ionutbalosin.training.ecommerce.product.api.model.ProductUpdateDto;
import ionutbalosin.training.ecommerce.product.entity.Product;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProductEntityMapper {

  public Product map(ProductCreateDto newProduct) {
    return new Product()
        .name(newProduct.getName())
        .brand(newProduct.getBrand())
        .category(newProduct.getCategory())
        .price(newProduct.getPrice().floatValue())
        .currency(newProduct.getCurrency().toString())
        .quantity(newProduct.getQuantity())
        .dateIns(LocalDateTime.now())
        .usrIns("anonymous")
        .stat("A");
  }

  public Product map(UUID productId, ProductUpdateDto productUpdateDto) {
    return new Product().id(productId).quantity(productUpdateDto.getQuantity());
  }
}
