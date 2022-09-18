package ionutbalosin.training.ecommerce.shopping.cart.dto.mapper;

import ionutbalosin.training.ecommerce.event.schema.order.ProductEvent;
import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;

public class ProductEventMapper {

  public ProductEvent map(ProductDto productDto, CartItem cartItem) {
    final ProductEvent productEvent = new ProductEvent();
    productEvent.setProductId(productDto.getProductId());
    productEvent.setName(productDto.getName());
    productEvent.setBrand(productDto.getBrand());
    productEvent.setPrice(productDto.getPrice().floatValue());
    productEvent.setQuantity(cartItem.getQuantity());
    productEvent.setDiscount(cartItem.getDiscount());
    return productEvent;
  }
}
