package ionutbalosin.training.ecommerce.order.dto.mapper;

import static java.math.BigDecimal.valueOf;

import ionutbalosin.training.ecommerce.order.api.model.OrderDto;
import ionutbalosin.training.ecommerce.order.model.Order;

public class OrderDtoMapper {

  public OrderDto map(Order order) {
    return new OrderDto()
        .orderId(order.getId())
        .userId(order.getUserId())
        .amount(valueOf(order.getAmount()))
        .currency(OrderDto.CurrencyEnum.fromValue(order.getCurrency()))
        .status(OrderDtoStatusMapper.map(order.getStatus()));
  }

  public enum OrderDtoStatusMapper {
    PAYMENT_INITIATED(OrderDto.StatusEnum.PAYMENT_INITIATED, Order.OrderStatus.PAYMENT_INITIATED),
    PAYMENT_APPROVED(OrderDto.StatusEnum.PAYMENT_APPROVED, Order.OrderStatus.PAYMENT_APPROVED),
    PAYMENT_FAILED(OrderDto.StatusEnum.PAYMENT_FAILED, Order.OrderStatus.PAYMENT_FAILED),
    SHIPPING(OrderDto.StatusEnum.SHIPPING, Order.OrderStatus.SHIPPING),
    COMPLETED(OrderDto.StatusEnum.COMPLETED, Order.OrderStatus.COMPLETED),
    CANCELLED(OrderDto.StatusEnum.CANCELLED, Order.OrderStatus.CANCELLED);

    private OrderDto.StatusEnum dtoStatus;
    private Order.OrderStatus modelStatus;

    OrderDtoStatusMapper(OrderDto.StatusEnum dtoStatus, Order.OrderStatus modelStatus) {
      this.dtoStatus = dtoStatus;
      this.modelStatus = modelStatus;
    }

    public static OrderDto.StatusEnum map(Order.OrderStatus modelStatus) {
      for (OrderDtoStatusMapper orderStatus : OrderDtoStatusMapper.values()) {
        if (orderStatus.modelStatus == modelStatus) {
          return orderStatus.dtoStatus;
        }
      }
      throw new IllegalArgumentException("Unexpected status '" + modelStatus + "'");
    }
  }
}
