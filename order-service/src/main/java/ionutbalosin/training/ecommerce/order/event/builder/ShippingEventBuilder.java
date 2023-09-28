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
import ionutbalosin.training.ecommerce.message.schema.shipping.ShippingTriggerCommand;
import ionutbalosin.training.ecommerce.order.model.Order;
import org.springframework.stereotype.Component;

@Component
public class ShippingEventBuilder {

  public ShippingTriggerCommand createCommand(Order order) {
    final ShippingTriggerCommand command = new ShippingTriggerCommand();
    command.setId(randomUUID());
    command.setOrderId(order.getId());
    command.setUserId(order.getUserId());
    final OrderCreatedEvent orderCreatedEvent =
        jsonObjectToObject(OrderCreatedEvent.class, order.getDetails());
    command.setProducts(orderCreatedEvent.getProducts());
    command.setAmount(order.getAmount());
    command.setCurrency(valueOf(order.getCurrency()));
    return command;
  }
}
