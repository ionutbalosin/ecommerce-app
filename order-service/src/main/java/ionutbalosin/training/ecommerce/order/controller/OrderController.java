package ionutbalosin.training.ecommerce.order.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;

import ionutbalosin.training.ecommerce.order.api.OrdersApi;
import ionutbalosin.training.ecommerce.order.api.model.OrderDetailsDto;
import ionutbalosin.training.ecommerce.order.api.model.OrderDto;
import ionutbalosin.training.ecommerce.order.api.model.OrderUpdateDto;
import ionutbalosin.training.ecommerce.order.dto.mapper.OrderDetailsDtoMapper;
import ionutbalosin.training.ecommerce.order.dto.mapper.OrderDtoMapper;
import ionutbalosin.training.ecommerce.order.dto.mapper.OrderMapper;
import ionutbalosin.training.ecommerce.order.model.Order;
import ionutbalosin.training.ecommerce.order.service.OrderService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class OrderController implements OrdersApi {

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
    final List<Order> orders = orderService.getOrders(userId);
    final List<OrderDto> ordersDto = orders.stream().map(orderDtoMapper::map).collect(toList());
    return new ResponseEntity<>(ordersDto, OK);
  }

  @Override
  public ResponseEntity<Void> ordersOrderIdPatch(UUID orderId, OrderUpdateDto orderUpdate) {
    final Order order = entityMapper.map(orderId, orderUpdate);
    orderService.updateOrder(order);
    return new ResponseEntity<>(OK);
  }

  @Override
  public ResponseEntity<OrderDetailsDto> ordersOrderIdGet(UUID orderId) {
    final Order order = orderService.getOrder(orderId);
    final OrderDetailsDto orderDetailsDto = orderDetailsDtoMapper.map(order);
    return new ResponseEntity<>(orderDetailsDto, OK);
  }
}
