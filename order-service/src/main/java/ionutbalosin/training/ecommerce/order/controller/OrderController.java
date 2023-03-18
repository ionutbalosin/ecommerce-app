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
package ionutbalosin.training.ecommerce.order.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;

import ionutbalosin.training.ecommerce.order.api.OrdersApi;
import ionutbalosin.training.ecommerce.order.api.model.OrderDetailsDto;
import ionutbalosin.training.ecommerce.order.api.model.OrderDto;
import ionutbalosin.training.ecommerce.order.api.model.OrderUpdateDto;
import ionutbalosin.training.ecommerce.order.dto.mapper.OrderDetailsDtoMapper;
import ionutbalosin.training.ecommerce.order.dto.mapper.OrderDtoMapper;
import ionutbalosin.training.ecommerce.order.model.Order;
import ionutbalosin.training.ecommerce.order.model.mapper.OrderMapper;
import ionutbalosin.training.ecommerce.order.service.OrderService;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class OrderController implements OrdersApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

  private final OrderService orderService;
  private final OrderMapper entityMapper;
  private final OrderDtoMapper orderDtoMapper;
  private final OrderDetailsDtoMapper orderDetailsDtoMapper;

  public OrderController(
      OrderService orderService,
      OrderMapper entityMapper,
      OrderDtoMapper orderDtoMapper,
      OrderDetailsDtoMapper orderDetailsDtoMapper) {
    this.orderService = orderService;
    this.entityMapper = entityMapper;
    this.orderDtoMapper = orderDtoMapper;
    this.orderDetailsDtoMapper = orderDetailsDtoMapper;
  }

  @Override
  public ResponseEntity<List<OrderDto>> ordersUserIdHistoryGet(UUID userId) {
    LOGGER.debug("ordersUserIdHistoryGet(userId = '{}')", userId);
    final List<Order> orders = orderService.getOrders(userId);
    final List<OrderDto> ordersDto = orders.stream().map(orderDtoMapper::map).collect(toList());
    return new ResponseEntity<>(ordersDto, OK);
  }

  @Override
  public ResponseEntity<Void> ordersOrderIdPatch(UUID orderId, OrderUpdateDto orderUpdate) {
    LOGGER.debug(
        "ordersOrderIdPatch(orderId = '{}', status = '{}')", orderId, orderUpdate.getStatus());
    final Order order = entityMapper.map(orderId, orderUpdate);
    orderService.updateOrder(order);
    return new ResponseEntity<>(OK);
  }

  @Override
  public ResponseEntity<OrderDetailsDto> ordersOrderIdGet(UUID orderId) {
    LOGGER.debug("ordersOrderIdGet(orderId = '{}')", orderId);
    final Order order = orderService.getOrder(orderId);
    final OrderDetailsDto orderDetailsDto = orderDetailsDtoMapper.map(order);
    return new ResponseEntity<>(orderDetailsDto, OK);
  }
}
