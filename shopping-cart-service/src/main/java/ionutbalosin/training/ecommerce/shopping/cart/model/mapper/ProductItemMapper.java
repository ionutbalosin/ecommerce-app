/**
 *  eCommerce Application
 *
 *  Copyright (c) 2022 - 2023 Ionut Balosin
 *  Website: www.ionutbalosin.com
 *  Twitter: @ionutbalosin / Mastodon: ionutbalosin@mastodon.socia
 *
 *
 *  MIT License
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */
package ionutbalosin.training.ecommerce.shopping.cart.model.mapper;

import static ionutbalosin.training.ecommerce.shopping.cart.model.ProductItem.CurrencyEnum.fromValue;
import static java.util.UUID.fromString;

import ionutbalosin.training.ecommerce.message.schema.product.ProductCdcValue;
import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import ionutbalosin.training.ecommerce.shopping.cart.model.ProductItem;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class ProductItemMapper {

  public ProductItem map(ProductCdcValue productCdc) {

    return new ProductItem()
        .productId(fromString(productCdc.getId()))
        .name(productCdc.getName())
        .brand(productCdc.getBrand())
        .category(productCdc.getCategory())
        .price(getPrice(productCdc.getPrice()).doubleValue())
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
