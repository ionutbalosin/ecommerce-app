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
package ionutbalosin.training.ecommerce.shopping.cart.event.builder;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import ionutbalosin.training.ecommerce.message.schema.currency.Currency;
import ionutbalosin.training.ecommerce.message.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.message.schema.product.ProductEvent;
import ionutbalosin.training.ecommerce.shopping.cart.event.mapper.ProductEventMapper;
import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;
import ionutbalosin.training.ecommerce.shopping.cart.model.ProductItem;
import ionutbalosin.training.ecommerce.shopping.cart.service.ProductService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;

@Component
public class OrderEventBuilder {

  private final ProductService productService;
  private final ProductEventMapper productEventMapper;

  public OrderEventBuilder(ProductService productService, ProductEventMapper productEventMapper) {
    this.productService = productService;
    this.productEventMapper = productEventMapper;
  }

  public OrderCreatedEvent createEvent(UUID userId, List<CartItem> cartItems) {
    final Map<UUID, CartItem> cartItemsMap = cartItemsAsMap(cartItems);
    final List<ProductItem> products = productService.getProducts(cartItemsMap.keySet());
    final AtomicReference<Double> amountRef = new AtomicReference<>(0.0);
    final AtomicReference<Currency> currencyRef = new AtomicReference<>();
    final List<ProductEvent> productEvents =
        createProductEvents(cartItemsMap, products, amountRef, currencyRef);

    return createEvent(userId, productEvents, amountRef.get(), currencyRef.get());
  }

  private Map<UUID, CartItem> cartItemsAsMap(List<CartItem> cartItems) {
    return cartItems.stream().collect(toMap(CartItem::getProductId, cartItem -> cartItem));
  }

  private List<ProductEvent> createProductEvents(
      Map<UUID, CartItem> cartItems,
      List<ProductItem> products,
      AtomicReference<Double> amountRef,
      AtomicReference<Currency> currencyRef) {

    return products.stream()
        .map(
            product -> {
              final CartItem cartItem = cartItems.get(product.getProductId());
              final ProductEvent productEvent = productEventMapper.map(product, cartItem);
              double productAmount = amountRef.get() + getProductAmount(productEvent);
              amountRef.set(productAmount);
              currencyRef.compareAndSet(null, productEvent.getCurrency());
              return productEvent;
            })
        .collect(toList());
  }

  private double getProductAmount(ProductEvent productEvent) {
    return (productEvent.getDiscount() / 100)
        * productEvent.getPrice()
        * productEvent.getQuantity();
  }

  private OrderCreatedEvent createEvent(
      UUID userId, List<ProductEvent> productEvents, double amount, Currency currency) {
    final OrderCreatedEvent event = new OrderCreatedEvent();
    event.setId(randomUUID());
    event.setUserId(userId);
    event.setProducts(productEvents);
    event.setCurrency(currency);
    event.setAmount(amount);

    return event;
  }
}
