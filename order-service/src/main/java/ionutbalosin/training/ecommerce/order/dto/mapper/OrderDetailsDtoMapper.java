package ionutbalosin.training.ecommerce.order.dto.mapper;

import static java.math.BigDecimal.valueOf;

import ionutbalosin.training.ecommerce.order.api.model.OrderDetailsDto;
import ionutbalosin.training.ecommerce.order.api.model.OrderDetailsDto.CurrencyEnum;
import ionutbalosin.training.ecommerce.order.api.model.OrderDetailsDto.StatusEnum;
import ionutbalosin.training.ecommerce.order.model.Order;
import ionutbalosin.training.ecommerce.order.model.OrderStatus;

public class OrderDetailsDtoMapper {

  public OrderDetailsDto map(Order order) {
    return new OrderDetailsDto()
        .orderId(order.getId())
        .userId(order.getUserId())
        .amount(valueOf(order.getAmount()))
        .currency(CurrencyEnum.fromValue(order.getCurrency()))
        .details(order.getDetails())
        .status(OrderToOrderDetailsDtoStatusMapper.map(order.getStatus()));
  }

  private enum OrderToOrderDetailsDtoStatusMapper {
    PAYMENT_INITIATED(StatusEnum.PAYMENT_INITIATED, OrderStatus.PAYMENT_INITIATED),
    PAYMENT_APPROVED(StatusEnum.PAYMENT_APPROVED, OrderStatus.PAYMENT_APPROVED),
    PAYMENT_FAILED(StatusEnum.PAYMENT_FAILED, OrderStatus.PAYMENT_FAILED),
    SHIPPING(StatusEnum.SHIPPING, OrderStatus.SHIPPING),
    COMPLETED(StatusEnum.COMPLETED, OrderStatus.COMPLETED),
    CANCELLED(StatusEnum.CANCELLED, OrderStatus.CANCELLED);

    private StatusEnum dtoStatus;
    private OrderStatus modelStatus;

    OrderToOrderDetailsDtoStatusMapper(StatusEnum dtoStatus, OrderStatus modelStatus) {
      this.dtoStatus = dtoStatus;
      this.modelStatus = modelStatus;
    }

    private static StatusEnum map(OrderStatus modelStatus) {
      for (OrderToOrderDetailsDtoStatusMapper orderStatus :
          OrderToOrderDetailsDtoStatusMapper.values()) {
        if (orderStatus.modelStatus == modelStatus) {
          return orderStatus.dtoStatus;
        }
      }
      throw new IllegalArgumentException("Unexpected status '" + modelStatus + "'");
    }
  }
}
