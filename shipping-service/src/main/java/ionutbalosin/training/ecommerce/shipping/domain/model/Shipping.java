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
package ionutbalosin.training.ecommerce.shipping.domain.model;

import java.util.List;
import java.util.UUID;

public class Shipping {

  private UUID userId;
  private UUID orderId;
  private double amount;
  private ShippingCurrency currency;
  private List<Product> products;
  private ShippingPriority priority;

  public UUID getUserId() {
    return userId;
  }

  public Shipping userId(UUID userId) {
    this.userId = userId;
    return this;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public Shipping orderId(UUID orderId) {
    this.orderId = orderId;
    return this;
  }

  public double getAmount() {
    return amount;
  }

  public Shipping amount(double amount) {
    this.amount = amount;
    return this;
  }

  public ShippingCurrency getCurrency() {
    return currency;
  }

  public Shipping currency(ShippingCurrency currency) {
    this.currency = currency;
    return this;
  }

  public List<Product> getProducts() {
    return products;
  }

  public Shipping products(List<Product> products) {
    this.products = products;
    return this;
  }

  public ShippingPriority getPriority() {
    return priority;
  }

  public Shipping setPriority(ShippingPriority priority) {
    this.priority = priority;
    return this;
  }

  public enum ShippingCurrency {
    EUR("EUR");

    private String value;

    ShippingCurrency(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static ShippingCurrency fromValue(String value) {
      for (ShippingCurrency b : ShippingCurrency.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  public enum ShippingPriority {
    NORMAL,
    FAST_DELIVERY
  }
}
