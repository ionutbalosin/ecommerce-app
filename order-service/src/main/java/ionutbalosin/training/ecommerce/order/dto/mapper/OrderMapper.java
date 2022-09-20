package ionutbalosin.training.ecommerce.order.dto.mapper;

import static ionutbalosin.training.ecommerce.order.model.Order.OrderStatus.PAYMENT_INITIATED;
import static ionutbalosin.training.ecommerce.order.util.JsonUtil.objectToJsonObject;

import ionutbalosin.training.ecommerce.event.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.order.api.model.OrderUpdateDto;
import ionutbalosin.training.ecommerce.order.model.Order;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderMapper {

  public Order map(OrderCreatedEvent event) {
    return new Order()
        .sourceEventId(event.getId())
        .userId(event.getUserId())
        .amount(event.getAmount())
        .currency(event.getCurrency().toString())
        .details(objectToJsonObject(event))
        .status(PAYMENT_INITIATED)
        .dateIns(LocalDateTime.now())
        .usrIns("anonymous")
        .stat("A");
  }

  public Order map(UUID orderId, OrderUpdateDto orderUpdate) {
    return new Order()
        .id(orderId)
        .status(OrderStatusMapper.map(orderUpdate.getStatus()))
        .usrUpd("anonymous")
        .dateUpd(LocalDateTime.now());
  }
}
