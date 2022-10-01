package ionutbalosin.training.ecommerce.order.model.mapper;

import static ionutbalosin.training.ecommerce.order.model.OrderStatus.PAYMENT_INITIATED;
import static ionutbalosin.training.ecommerce.order.util.JsonUtil.objectToJsonObject;

import ionutbalosin.training.ecommerce.message.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentTriggeredEvent;
import ionutbalosin.training.ecommerce.order.api.model.OrderUpdateDto;
import ionutbalosin.training.ecommerce.order.model.Order;
import ionutbalosin.training.ecommerce.order.model.OrderStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
public class OrderMapper {

  public Order map(OrderCreatedEvent orderEvent) {
    return new Order()
        .sourceEventId(orderEvent.getId())
        .userId(orderEvent.getUserId())
        .amount(orderEvent.getAmount())
        .currency(orderEvent.getCurrency().toString())
        .details(objectToJsonObject(orderEvent))
        .status(PAYMENT_INITIATED)
        .dateIns(LocalDateTime.now())
        .usrIns("anonymous")
        .stat("A");
  }

  public Order map(UUID orderId, OrderUpdateDto orderUpdate) {
    return new Order()
        .id(orderId)
        .status(OrderUpdateToOrderStatusMapper.map(orderUpdate.getStatus()))
        .usrUpd("anonymous")
        .dateUpd(LocalDateTime.now());
  }

  public Order map(PaymentTriggeredEvent paymentEvent) {
    return new Order()
        .id(paymentEvent.getOrderId())
        .userId(paymentEvent.getUserId())
        .status(PaymentToOrderStatusMapper.map(paymentEvent.getStatus()))
        .usrUpd("anonymous")
        .dateUpd(LocalDateTime.now());
  }

  private enum OrderUpdateToOrderStatusMapper {
    PAYMENT_INITIATED(OrderUpdateDto.StatusEnum.PAYMENT_INITIATED, OrderStatus.PAYMENT_INITIATED),
    PAYMENT_APPROVED(OrderUpdateDto.StatusEnum.PAYMENT_APPROVED, OrderStatus.PAYMENT_APPROVED),
    PAYMENT_FAILED(OrderUpdateDto.StatusEnum.PAYMENT_FAILED, OrderStatus.PAYMENT_FAILED),
    SHIPPING(OrderUpdateDto.StatusEnum.SHIPPING, OrderStatus.SHIPPING),
    COMPLETED(OrderUpdateDto.StatusEnum.COMPLETED, OrderStatus.COMPLETED),
    CANCELLED(OrderUpdateDto.StatusEnum.CANCELLED, OrderStatus.CANCELLED);

    private OrderUpdateDto.StatusEnum dtoStatus;
    private OrderStatus modelStatus;

    OrderUpdateToOrderStatusMapper(OrderUpdateDto.StatusEnum dtoStatus, OrderStatus modelStatus) {
      this.dtoStatus = dtoStatus;
      this.modelStatus = modelStatus;
    }

    private static OrderStatus map(OrderUpdateDto.StatusEnum dtoStatus) {
      for (OrderUpdateToOrderStatusMapper orderStatus : OrderUpdateToOrderStatusMapper.values()) {
        if (orderStatus.dtoStatus == dtoStatus) {
          return orderStatus.modelStatus;
        }
      }
      throw new IllegalArgumentException("Unexpected status '" + dtoStatus + "'");
    }
  }

  private enum PaymentToOrderStatusMapper {
    PAYMENT_APPROVED(PaymentStatus.APPROVED, OrderStatus.PAYMENT_APPROVED),
    PAYMENT_FAILED(PaymentStatus.FAILED, OrderStatus.PAYMENT_FAILED);

    private PaymentStatus dtoStatus;
    private OrderStatus modelStatus;

    PaymentToOrderStatusMapper(PaymentStatus dtoStatus, OrderStatus modelStatus) {
      this.dtoStatus = dtoStatus;
      this.modelStatus = modelStatus;
    }

    private static OrderStatus map(PaymentStatus dtoStatus) {
      for (PaymentToOrderStatusMapper orderStatus : PaymentToOrderStatusMapper.values()) {
        if (orderStatus.dtoStatus == dtoStatus) {
          return orderStatus.modelStatus;
        }
      }
      throw new IllegalArgumentException("Unexpected status '" + dtoStatus + "'");
    }
  }
}
