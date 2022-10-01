package ionutbalosin.training.ecommerce.shopping.cart.model.mapper;

import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemCreateDto;
import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;
import java.time.LocalDateTime;
import java.util.UUID;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
public class CartItemMapper {

  public CartItem map(UUID userId, CartItemCreateDto cartItemCreate) {
    return new CartItem()
        .userId(userId)
        .productId(cartItemCreate.getProductId())
        .quantity(cartItemCreate.getQuantity())
        .discount(cartItemCreate.getDiscount().floatValue())
        .dateIns(LocalDateTime.now())
        .usrIns("anonymous")
        .stat("A");
  }
}
