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
package ionutbalosin.training.ecommerce.shopping.cart.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;

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
