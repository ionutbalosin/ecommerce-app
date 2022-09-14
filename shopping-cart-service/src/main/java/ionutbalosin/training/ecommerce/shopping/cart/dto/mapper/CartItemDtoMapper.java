package ionutbalosin.training.ecommerce.shopping.cart.dto.mapper;

import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemDto;
import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemIdDto;
import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

public class CartItemDtoMapper {

  public CartItemDto map(CartItem cartItem) {
    return new CartItemDto()
        .itemId(cartItem.getId())
        .productId(cartItem.getProductId())
        .quantity(cartItem.getQuantity())
        .discount(BigDecimal.valueOf(cartItem.getDiscount()));
  }

  public List<CartItemIdDto> map(Set<UUID> uuids) {
    return uuids.stream().map(uuid -> new CartItemIdDto().itemId(uuid)).collect(toList());
  }
}
