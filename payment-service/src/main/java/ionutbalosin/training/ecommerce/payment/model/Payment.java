/**
 *  eCommerce Application
 *
 *  Copyright (c) 2022 - 2024 Ionut Balosin
 *  Website: www.ionutbalosin.com
 *  X: @ionutbalosin | LinkedIn: ionutbalosin | Mastodon: ionutbalosin@mastodon.social
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
package ionutbalosin.training.ecommerce.payment.model;

import java.util.UUID;

public class Payment {

  private UUID userId;
  private UUID orderId;
  private double amount;
  private PaymentCurrency currency;

  public UUID getUserId() {
    return userId;
  }

  public Payment userId(UUID userId) {
    this.userId = userId;
    return this;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public Payment orderId(UUID orderId) {
    this.orderId = orderId;
    return this;
  }

  public double getAmount() {
    return amount;
  }

  public Payment amount(double amount) {
    this.amount = amount;
    return this;
  }

  public PaymentCurrency getCurrency() {
    return currency;
  }

  public Payment currency(PaymentCurrency currency) {
    this.currency = currency;
    return this;
  }

  public enum PaymentCurrency {
    EUR("EUR");

    private String value;

    PaymentCurrency(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public static PaymentCurrency fromValue(String value) {
      for (PaymentCurrency b : PaymentCurrency.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }
}
