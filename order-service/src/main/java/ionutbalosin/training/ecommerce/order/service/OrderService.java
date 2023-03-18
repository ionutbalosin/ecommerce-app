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
package ionutbalosin.training.ecommerce.order.service;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import ionutbalosin.training.ecommerce.order.dao.OrderJdbcDao;
import ionutbalosin.training.ecommerce.order.model.Order;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrderService {

  private final OrderJdbcDao orderJdbcDao;

  public OrderService(OrderJdbcDao orderJdbcDao) {
    this.orderJdbcDao = orderJdbcDao;
  }

  public List<Order> getOrders(UUID userId) {
    return orderJdbcDao.getAll(userId);
  }

  public int updateOrder(Order order) {
    return orderJdbcDao.update(order);
  }

  public UUID createOrder(Order order) {
    return orderJdbcDao.save(order);
  }

  public Order getOrder(UUID orderId) {
    return orderJdbcDao
        .get(orderId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Not found order id " + orderId));
  }
}
