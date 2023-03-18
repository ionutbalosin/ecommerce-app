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
package ionutbalosin.training.ecommerce.shopping.cart.service;

import ionutbalosin.training.ecommerce.message.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.shopping.cart.dao.CartItemJdbcDao;
import ionutbalosin.training.ecommerce.shopping.cart.event.builder.OrderEventBuilder;
import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;
import ionutbalosin.training.ecommerce.shopping.cart.sender.OrderEventSender;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

  private final CartItemJdbcDao cartItemJdbcDao;
  private final OrderEventBuilder orderEventBuilder;
  private final OrderEventSender orderEventSender;

  public CartService(
      CartItemJdbcDao cartItemJdbcDao,
      OrderEventBuilder orderEventBuilder,
      OrderEventSender orderEventSender) {
    this.cartItemJdbcDao = cartItemJdbcDao;
    this.orderEventBuilder = orderEventBuilder;
    this.orderEventSender = orderEventSender;
  }

  @Transactional
  public void createCartItems(Collection<CartItem> cartItem) {
    cartItemJdbcDao.saveAll(cartItem);
  }

  public int deleteCartItems(UUID userId) {
    return cartItemJdbcDao.deleteAll(userId);
  }

  public List<CartItem> getCartItems(UUID userId) {
    return cartItemJdbcDao.getAll(userId);
  }

  @Async
  public void checkoutCartItems(UUID userId, List<CartItem> cartItems) {
    final OrderCreatedEvent event = orderEventBuilder.createEvent(userId, cartItems);
    orderEventSender.send(event);
  }
}
