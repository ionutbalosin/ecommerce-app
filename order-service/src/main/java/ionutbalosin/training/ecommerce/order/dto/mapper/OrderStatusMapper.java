package ionutbalosin.training.ecommerce.order.dto.mapper;

import ionutbalosin.training.ecommerce.order.api.model.OrderUpdateDto.StatusEnum;
import ionutbalosin.training.ecommerce.order.model.Order.OrderStatus;

public enum OrderStatusMapper {
  PAYMENT_INITIATED(StatusEnum.PAYMENT_INITIATED, OrderStatus.PAYMENT_INITIATED),
  PAYMENT_APPROVED(StatusEnum.PAYMENT_APPROVED, OrderStatus.PAYMENT_APPROVED),
  PAYMENT_FAILED(StatusEnum.PAYMENT_FAILED, OrderStatus.PAYMENT_FAILED),
  SHIPPING(StatusEnum.SHIPPING, OrderStatus.SHIPPING),
  COMPLETED(StatusEnum.COMPLETED, OrderStatus.COMPLETED),
  CANCELLED(StatusEnum.CANCELLED, OrderStatus.CANCELLED);

  private StatusEnum dtoStatus;
  private OrderStatus modelStatus;

  OrderStatusMapper(StatusEnum dtoStatus, OrderStatus modelStatus) {
    this.dtoStatus = dtoStatus;
    this.modelStatus = modelStatus;
  }

  public static OrderStatus mapStatus(StatusEnum dtoStatus) {
    for (OrderStatusMapper orderStatus : OrderStatusMapper.values()) {
      if (orderStatus.dtoStatus == dtoStatus) {
        return orderStatus.modelStatus;
      }
    }
    throw new IllegalArgumentException("Unexpected status '" + dtoStatus + "'");
  }
}
