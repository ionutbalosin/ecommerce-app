package ionutbalosin.training.ecommerce.shopping.cart.dto.mapper;

import static ionutbalosin.training.ecommerce.message.schema.order.OrderCurrency.valueOf;

import ionutbalosin.training.ecommerce.message.schema.order.ProductEvent;
import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;
import ionutbalosin.training.ecommerce.shopping.cart.model.ProductItem;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
public class ProductEventMapper {

  public ProductEvent map(ProductItem productItem, CartItem cartItem) {
    final ProductEvent productEvent = new ProductEvent();
    productEvent.setProductId(productItem.getProductId());
    productEvent.setName(productItem.getName());
    productEvent.setBrand(productItem.getBrand());
    productEvent.setPrice(productItem.getPrice().floatValue());
    productEvent.setQuantity(cartItem.getQuantity());
    productEvent.setDiscount(cartItem.getDiscount());
    productEvent.setCurrency(valueOf(productItem.getCurrency().getValue()));
    return productEvent;
  }
}
