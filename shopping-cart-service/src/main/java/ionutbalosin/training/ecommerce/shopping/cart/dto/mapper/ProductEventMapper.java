package ionutbalosin.training.ecommerce.shopping.cart.dto.mapper;

import static ionutbalosin.training.ecommerce.message.schema.order.OrderCurrency.valueOf;

import ionutbalosin.training.ecommerce.message.schema.order.ProductEvent;
import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
public class ProductEventMapper {

  public ProductEvent map(ProductDto productDto, CartItem cartItem) {
    final ProductEvent productEvent = new ProductEvent();
    productEvent.setProductId(productDto.getProductId());
    productEvent.setName(productDto.getName());
    productEvent.setBrand(productDto.getBrand());
    productEvent.setPrice(productDto.getPrice().floatValue());
    productEvent.setQuantity(cartItem.getQuantity());
    productEvent.setDiscount(cartItem.getDiscount());
    productEvent.setCurrency(valueOf(productDto.getCurrency().getValue()));
    return productEvent;
  }
}
