/**
 *  eCommerce Application
 *
 *  Copyright (c) 2022 - 2023 Ionut Balosin
 *  Website: www.ionutbalosin.com
 *  Twitter: @ionutbalosin / Mastodon: ionutbalosin@mastodon.socia
 *
 *
 *  MIT License
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */
package ionutbalosin.training.ecommerce.order.model.mapper;

import static ionutbalosin.training.ecommerce.order.util.JsonUtil.objectToJsonObject;

import ionutbalosin.training.ecommerce.message.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus;
import ionutbalosin.training.ecommerce.order.api.model.OrderUpdateDto;
import ionutbalosin.training.ecommerce.order.model.Order;
import ionutbalosin.training.ecommerce.order.model.OrderStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderMapper {

  public Order map(OrderCreatedEvent orderEvent) {
    return new Order()
        .sourceEventId(orderEvent.getId())
        .userId(orderEvent.getUserId())
        .amount(orderEvent.getAmount())
        .currency(orderEvent.getCurrency().toString())
        .details(objectToJsonObject(orderEvent))
        .status(OrderStatus.NEW)
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

  public Order map(Order order, OrderStatus status) {
    return new Order()
        .id(order.getId())
        .userId(order.getUserId())
        .status(status)
        .usrUpd("anonymous")
        .dateUpd(LocalDateTime.now());
  }

  public Order map(Order order, ShippingStatus shippingStatus) {
    return new Order()
        .id(order.getId())
        .userId(order.getUserId())
        .status(ShippingToOrderStatusMapper.map(shippingStatus))
        .usrUpd("anonymous")
        .dateUpd(LocalDateTime.now());
  }

  private enum OrderUpdateToOrderStatusMapper {
    NEW(OrderUpdateDto.StatusEnum.NEW, OrderStatus.NEW),
    PAYMENT_APPROVED(OrderUpdateDto.StatusEnum.PAYMENT_APPROVED, OrderStatus.PAYMENT_APPROVED),
    PAYMENT_FAILED(OrderUpdateDto.StatusEnum.PAYMENT_FAILED, OrderStatus.PAYMENT_FAILED),
    SHIPPING_IN_PROGRESS(
        OrderUpdateDto.StatusEnum.SHIPPING_IN_PROGRESS, OrderStatus.SHIPPING_IN_PROGRESS),
    SHIPPING_COMPLETED(
        OrderUpdateDto.StatusEnum.SHIPPING_COMPLETED, OrderStatus.SHIPPING_COMPLETED),
    SHIPPING_FAILED(OrderUpdateDto.StatusEnum.SHIPPING_FAILED, OrderStatus.SHIPPING_FAILED),
    COMPLETED(OrderUpdateDto.StatusEnum.COMPLETED, OrderStatus.COMPLETED),
    CANCELLED(OrderUpdateDto.StatusEnum.CANCELLED, OrderStatus.CANCELLED);

    private final OrderUpdateDto.StatusEnum dtoStatus;
    private final OrderStatus modelStatus;

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

  private enum ShippingToOrderStatusMapper {
    SHIPPING_IN_PROGRESS(ShippingStatus.IN_PROGRESS, OrderStatus.SHIPPING_IN_PROGRESS),
    SHIPPING_COMPLETED(ShippingStatus.COMPLETED, OrderStatus.SHIPPING_COMPLETED),
    SHIPPING_FAILED(ShippingStatus.FAILED, OrderStatus.SHIPPING_FAILED);

    private final ShippingStatus dtoStatus;
    private final OrderStatus modelStatus;

    ShippingToOrderStatusMapper(ShippingStatus dtoStatus, OrderStatus modelStatus) {
      this.dtoStatus = dtoStatus;
      this.modelStatus = modelStatus;
    }

    private static OrderStatus map(ShippingStatus dtoStatus) {
      for (ShippingToOrderStatusMapper shippingStatus : ShippingToOrderStatusMapper.values()) {
        if (shippingStatus.dtoStatus == dtoStatus) {
          return shippingStatus.modelStatus;
        }
      }
      throw new IllegalArgumentException("Unexpected status '" + dtoStatus + "'");
    }
  }
}
