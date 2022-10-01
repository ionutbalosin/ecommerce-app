package ionutbalosin.training.ecommerce.shopping.cart.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
public class ProductItem {

  private UUID productId;
  private String name;
  private String brand;
  private String category;
  private double price;
  private CurrencyEnum currency;
  private Integer quantity;

  public UUID getProductId() {
    return productId;
  }

  public ProductItem productId(UUID productId) {
    this.productId = productId;
    return this;
  }

  public String getName() {
    return name;
  }

  public ProductItem name(String name) {
    this.name = name;
    return this;
  }

  public String getBrand() {
    return brand;
  }

  public ProductItem brand(String brand) {
    this.brand = brand;
    return this;
  }

  public String getCategory() {
    return category;
  }

  public ProductItem category(String category) {
    this.category = category;
    return this;
  }

  public double getPrice() {
    return price;
  }

  public ProductItem price(double price) {
    this.price = price;
    return this;
  }

  public CurrencyEnum getCurrency() {
    return currency;
  }

  public ProductItem currency(CurrencyEnum currency) {
    this.currency = currency;
    return this;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public ProductItem quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  public enum CurrencyEnum {
    EUR("EUR");

    private String value;

    CurrencyEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static CurrencyEnum fromValue(String value) {
      for (CurrencyEnum b : CurrencyEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }
}
