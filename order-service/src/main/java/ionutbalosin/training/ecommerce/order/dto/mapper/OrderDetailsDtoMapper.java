package ionutbalosin.training.ecommerce.order.dto.mapper;

import static java.math.BigDecimal.valueOf;

import ionutbalosin.training.ecommerce.order.api.model.OrderDetailsDto;
import ionutbalosin.training.ecommerce.order.model.Order;

public class OrderDetailsDtoMapper {

  public OrderDetailsDto map(Order order) {
    return new OrderDetailsDto()
        .orderId(order.getId())
        .userId(order.getUserId())
        .amount(valueOf(order.getAmount()))
        .currency(OrderDetailsDto.CurrencyEnum.fromValue(order.getCurrency()))
        .details(order.getDetails())
        .status(OrderDetailsDtoStatusMapper.map(order.getStatus()));
  }

  public enum OrderDetailsDtoStatusMapper {
    PAYMENT_INITIATED(
        OrderDetailsDto.StatusEnum.PAYMENT_INITIATED, Order.OrderStatus.PAYMENT_INITIATED),
    PAYMENT_APPROVED(
        OrderDetailsDto.StatusEnum.PAYMENT_APPROVED, Order.OrderStatus.PAYMENT_APPROVED),
    PAYMENT_FAILED(OrderDetailsDto.StatusEnum.PAYMENT_FAILED, Order.OrderStatus.PAYMENT_FAILED),
    SHIPPING(OrderDetailsDto.StatusEnum.SHIPPING, Order.OrderStatus.SHIPPING),
    COMPLETED(OrderDetailsDto.StatusEnum.COMPLETED, Order.OrderStatus.COMPLETED),
    CANCELLED(OrderDetailsDto.StatusEnum.CANCELLED, Order.OrderStatus.CANCELLED);

    private OrderDetailsDto.StatusEnum dtoStatus;
    private Order.OrderStatus modelStatus;

    OrderDetailsDtoStatusMapper(
        OrderDetailsDto.StatusEnum dtoStatus, Order.OrderStatus modelStatus) {
      this.dtoStatus = dtoStatus;
      this.modelStatus = modelStatus;
    }

    public static OrderDetailsDto.StatusEnum map(Order.OrderStatus modelStatus) {
      for (OrderDetailsDtoStatusMapper orderStatus : OrderDetailsDtoStatusMapper.values()) {
        if (orderStatus.modelStatus == modelStatus) {
          return orderStatus.dtoStatus;
        }
      }
      throw new IllegalArgumentException("Unexpected status '" + modelStatus + "'");
    }
  }
}
