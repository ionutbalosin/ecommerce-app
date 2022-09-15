package ionutbalosin.training.ecommerce.shopping.cart.model.mapper;

import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemCreateDto;
import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;
import java.time.LocalDateTime;
import java.util.UUID;

public class CartItemMapper {

  public CartItem map(UUID userId, CartItemCreateDto newCartItem) {
    return new CartItem()
        .userId(userId)
        .productId(newCartItem.getProductId())
        .quantity(newCartItem.getQuantity())
        .discount(newCartItem.getDiscount().floatValue())
        .dateIns(LocalDateTime.now())
        .usrIns("anonymous")
        .stat("A");
  }
}
