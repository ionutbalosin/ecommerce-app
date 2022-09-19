package ionutbalosin.training.ecommerce.order.dto.mapper;

import static ionutbalosin.training.ecommerce.order.api.model.OrderDto.CurrencyEnum.fromValue;
import static ionutbalosin.training.ecommerce.order.dto.mapper.OrderDtoStatusMapper.mapStatus;
import static java.math.BigDecimal.valueOf;

import ionutbalosin.training.ecommerce.order.api.model.OrderDto;
import ionutbalosin.training.ecommerce.order.model.Order;

public class OrderDtoMapper {

  public OrderDto map(Order order) {
    return new OrderDto()
        .orderId(order.getId())
        .userId(order.getUserId())
        .amount(valueOf(order.getAmount()))
        .currency(fromValue(order.getCurrency()))
        .status(mapStatus(order.getStatus()));
  }
}
