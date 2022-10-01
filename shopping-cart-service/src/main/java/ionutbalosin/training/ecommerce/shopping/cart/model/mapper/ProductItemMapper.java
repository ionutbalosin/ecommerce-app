package ionutbalosin.training.ecommerce.shopping.cart.model.mapper;

import static ionutbalosin.training.ecommerce.shopping.cart.model.ProductItem.CurrencyEnum.fromValue;
import static java.util.UUID.fromString;

import ionutbalosin.training.ecommerce.message.schema.product.ProductCdcValue;
import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import ionutbalosin.training.ecommerce.shopping.cart.model.ProductItem;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
public class ProductItemMapper {

  public ProductItem map(ProductCdcValue productCdc) {

    return new ProductItem()
        .productId(fromString(productCdc.getId()))
        .name(productCdc.getName())
        .brand(productCdc.getBrand())
        .category(productCdc.getCategory())
        .price(getPrice(productCdc.getPrice()))
        .currency(fromValue(productCdc.getCurrency()))
        .quantity(productCdc.getQuantity());
  }

  public ProductItem map(ProductDto product) {
    return new ProductItem()
        .productId(product.getProductId())
        .name(product.getName())
        .brand(product.getBrand())
        .category(product.getCategory())
        .price(product.getPrice())
        .currency(fromValue(product.getCurrency().getValue()))
        .quantity(product.getQuantity());
  }

  private BigDecimal getPrice(ByteBuffer byteBuffer) {
    byte[] array = new byte[byteBuffer.remaining()];
    byteBuffer.get(array);
    return new BigDecimal(new BigInteger(array), 2);
  }
}
