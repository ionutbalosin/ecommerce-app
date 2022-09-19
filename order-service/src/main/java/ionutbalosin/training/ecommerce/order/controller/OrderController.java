package ionutbalosin.training.ecommerce.order.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static org.springframework.http.HttpStatus.OK;

import ionutbalosin.training.ecommerce.order.api.OrdersApi;
import ionutbalosin.training.ecommerce.order.api.model.OrderDetailsDto;
import ionutbalosin.training.ecommerce.order.api.model.OrderDto;
import ionutbalosin.training.ecommerce.order.api.model.OrderUpdateDto;
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
  private final OrderDtoMapper dtoMapper;

  public OrderController(
      OrderService orderService, OrderMapper entityMapper, OrderDtoMapper dtoMapper) {
    this.orderService = orderService;
    this.entityMapper = entityMapper;
    this.dtoMapper = dtoMapper;
  }

  @Override
  public ResponseEntity<List<OrderDto>> ordersUserIdHistoryGet(UUID userId) {
    final List<Order> orders = orderService.getOrders(userId);
    final List<OrderDto> ordersDto = orders.stream().map(dtoMapper::map).collect(toList());
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
    return new ResponseEntity<>(NOT_IMPLEMENTED);
  }
}
