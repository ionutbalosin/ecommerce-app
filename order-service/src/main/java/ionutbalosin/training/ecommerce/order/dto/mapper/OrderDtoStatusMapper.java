package ionutbalosin.training.ecommerce.order.dto.mapper;

import ionutbalosin.training.ecommerce.order.api.model.OrderDto.StatusEnum;
import ionutbalosin.training.ecommerce.order.model.Order.OrderStatus;

public enum OrderDtoStatusMapper {
  PAYMENT_INITIATED(StatusEnum.PAYMENT_INITIATED, OrderStatus.PAYMENT_INITIATED),
  PAYMENT_APPROVED(StatusEnum.PAYMENT_APPROVED, OrderStatus.PAYMENT_APPROVED),
  PAYMENT_FAILED(StatusEnum.PAYMENT_FAILED, OrderStatus.PAYMENT_FAILED),
  SHIPPING(StatusEnum.SHIPPING, OrderStatus.SHIPPING),
  COMPLETED(StatusEnum.COMPLETED, OrderStatus.COMPLETED),
  CANCELLED(StatusEnum.CANCELLED, OrderStatus.CANCELLED);

  private StatusEnum dtoStatus;
  private OrderStatus modelStatus;

  OrderDtoStatusMapper(StatusEnum dtoStatus, OrderStatus modelStatus) {
    this.dtoStatus = dtoStatus;
    this.modelStatus = modelStatus;
  }

  public static StatusEnum mapStatus(OrderStatus modelStatus) {
    for (OrderDtoStatusMapper orderStatus : OrderDtoStatusMapper.values()) {
      if (orderStatus.modelStatus == modelStatus) {
        return orderStatus.dtoStatus;
      }
    }
    throw new IllegalArgumentException("Unexpected status '" + modelStatus + "'");
  }
}
