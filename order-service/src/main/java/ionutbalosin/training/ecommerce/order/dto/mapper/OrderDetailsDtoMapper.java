package ionutbalosin.training.ecommerce.order.dto.mapper;

import static ionutbalosin.training.ecommerce.order.api.model.OrderDetailsDto.CurrencyEnum.fromValue;

import ionutbalosin.training.ecommerce.order.api.model.OrderDetailsDto;
import ionutbalosin.training.ecommerce.order.api.model.OrderDetailsDto.StatusEnum;
import ionutbalosin.training.ecommerce.order.model.Order;
import ionutbalosin.training.ecommerce.order.model.OrderStatus;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
public class OrderDetailsDtoMapper {

  public OrderDetailsDto map(Order order) {
    return new OrderDetailsDto()
        .orderId(order.getId())
        .userId(order.getUserId())
        .amount(order.getAmount())
        .currency(fromValue(order.getCurrency()))
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
