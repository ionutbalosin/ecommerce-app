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
package ionutbalosin.training.ecommerce.shipping.adapter.listener.mapper;

import static ionutbalosin.training.ecommerce.shipping.domain.model.Shipping.ShippingCurrency.fromValue;
import static ionutbalosin.training.ecommerce.shipping.domain.model.Shipping.ShippingPriority.NORMAL;
import static java.util.stream.Collectors.toList;

import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingTriggerCommand;
import ionutbalosin.training.ecommerce.shipping.domain.model.Product;
import ionutbalosin.training.ecommerce.shipping.domain.model.Shipping;
import java.util.List;

public class ShippingMapper {

  private final ProductMapper productMapper;

  public ShippingMapper(ProductMapper productMapper) {
    this.productMapper = productMapper;
  }

  public Shipping map(ShippingTriggerCommand shippingCommand) {
    return new Shipping()
        .userId(shippingCommand.getUserId())
        .orderId(shippingCommand.getOrderId())
        .amount(shippingCommand.getAmount())
        .currency(fromValue(shippingCommand.getCurrency().toString()))
        .products(createProducts(shippingCommand))
        .setPriority(NORMAL);
  }

  private List<Product> createProducts(ShippingTriggerCommand shippingCommand) {
    return shippingCommand.getProducts().stream()
        .map(
            productEvent -> {
              final Product product = productMapper.map(productEvent);
              return product;
            })
        .collect(toList());
  }
}
