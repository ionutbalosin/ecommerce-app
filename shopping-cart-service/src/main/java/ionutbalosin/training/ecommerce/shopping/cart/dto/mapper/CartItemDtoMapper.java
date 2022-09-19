package ionutbalosin.training.ecommerce.shopping.cart.dto.mapper;

import static java.math.BigDecimal.valueOf;
import static java.util.stream.Collectors.toList;

import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemDto;
import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemIdDto;
import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CartItemDtoMapper {

  public CartItemDto map(CartItem cartItem) {
    return new CartItemDto()
        .itemId(cartItem.getId())
        .productId(cartItem.getProductId())
        .quantity(cartItem.getQuantity())
        .discount(valueOf(cartItem.getDiscount()));
  }

  public List<CartItemIdDto> map(Set<UUID> uuids) {
    return uuids.stream().map(uuid -> new CartItemIdDto().itemId(uuid)).collect(toList());
  }
}
