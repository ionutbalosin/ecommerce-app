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
package ionutbalosin.training.ecommerce.order.event.builder;

import static ionutbalosin.training.ecommerce.message.schema.currency.Currency.valueOf;
import static ionutbalosin.training.ecommerce.order.util.JsonUtil.jsonObjectToObject;
import static java.util.UUID.randomUUID;

import ionutbalosin.training.ecommerce.message.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.message.schema.order.OrderStatus;
import ionutbalosin.training.ecommerce.message.schema.order.OrderStatusUpdatedEvent;
import ionutbalosin.training.ecommerce.message.schema.payment.PaymentStatus;
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingStatus;
import ionutbalosin.training.ecommerce.order.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderEventBuilder {

  public OrderStatusUpdatedEvent createEvent(Order order, PaymentStatus paymentStatus) {
    final OrderStatusUpdatedEvent event = new OrderStatusUpdatedEvent();
    event.setId(randomUUID());
    event.setOrderId(order.getId());
    event.setUserId(order.getUserId());
    final OrderCreatedEvent orderCreatedEvent =
        jsonObjectToObject(OrderCreatedEvent.class, order.getDetails());
    event.setProducts(orderCreatedEvent.getProducts());
    event.setAmount(order.getAmount());
    event.setCurrency(valueOf(order.getCurrency()));
    event.setStatus(PaymentToOrderStatusMapper.map(paymentStatus));
    return event;
  }

  public OrderStatusUpdatedEvent createEvent(Order order, ShippingStatus shippingStatus) {
    final OrderStatusUpdatedEvent event = new OrderStatusUpdatedEvent();
    event.setId(randomUUID());
    event.setOrderId(order.getId());
    event.setUserId(order.getUserId());
    final OrderCreatedEvent orderCreatedEvent =
        jsonObjectToObject(OrderCreatedEvent.class, order.getDetails());
    event.setProducts(orderCreatedEvent.getProducts());
    event.setAmount(order.getAmount());
    event.setCurrency(valueOf(order.getCurrency()));
    event.setStatus(ShippingToOrderStatusMapper.map(shippingStatus));
    return event;
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

    public static OrderStatus map(PaymentStatus dtoStatus) {
      for (PaymentToOrderStatusMapper orderStatus : PaymentToOrderStatusMapper.values()) {
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
