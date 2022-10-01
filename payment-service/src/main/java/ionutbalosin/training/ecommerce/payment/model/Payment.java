package ionutbalosin.training.ecommerce.payment.model;

import java.util.UUID;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
public class Payment {

  private UUID userId;
  private UUID orderId;
  private String description;
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

  public String getDescription() {
    return description;
  }

  public Payment description(String description) {
    this.description = description;
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
