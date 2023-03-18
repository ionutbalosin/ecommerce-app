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
package ionutbalosin.training.ecommerce.product.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Product {

  public static final String ID = "ID";
  public static final String NAME = "NAME";
  public static final String BRAND = "BRAND";
  public static final String CATEGORY = "CATEGORY";
  public static final String PRICE = "PRICE";
  public static final String CURRENCY = "CURRENCY";
  public static final String QUANTITY = "QUANTITY";
  public static final String DAT_INS = "DAT_INS";
  public static final String DAT_UPD = "DAT_UPD";
  public static final String USR_INS = "USR_INS";
  public static final String USR_UPD = "USR_UPD";
  public static final String STAT = "STAT";

  private UUID id;
  private String name;
  private String brand;
  private String category;
  private double price;
  private String currency;
  private Integer quantity;
  private LocalDateTime dateIns;
  private LocalDateTime dateUpd;
  private String usrIns;
  private String usrUpd;
  private String stat;

  public UUID getId() {
    return id;
  }

  public Product id(UUID productId) {
    this.id = productId;
    return this;
  }

  public String getName() {
    return name;
  }

  public Product name(String name) {
    this.name = name;
    return this;
  }

  public String getBrand() {
    return brand;
  }

  public Product brand(String brand) {
    this.brand = brand;
    return this;
  }

  public String getCategory() {
    return category;
  }

  public Product category(String category) {
    this.category = category;
    return this;
  }

  public double getPrice() {
    return price;
  }

  public Product price(double price) {
    this.price = price;
    return this;
  }

  public String getCurrency() {
    return currency;
  }

  public Product currency(String currency) {
    this.currency = currency;
    return this;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public Product quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  public LocalDateTime getDateIns() {
    return dateIns;
  }

  public Product dateIns(LocalDateTime dateIns) {
    this.dateIns = dateIns;
    return this;
  }

  public LocalDateTime getDateUpd() {
    return dateUpd;
  }

  public Product dateUpd(LocalDateTime dateUpd) {
    this.dateUpd = dateUpd;
    return this;
  }

  public String getUsrIns() {
    return usrIns;
  }

  public Product usrIns(String usrIns) {
    this.usrIns = usrIns;
    return this;
  }

  public String getUsrUpd() {
    return usrUpd;
  }

  public Product usrUpd(String usrUpd) {
    this.usrUpd = usrUpd;
    return this;
  }

  public String getStat() {
    return stat;
  }

  public Product stat(String stat) {
    this.stat = stat;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Product product)) return false;
    return name.equals(product.name)
        && brand.equals(product.brand)
        && category.equals(product.category);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, brand, category);
  }
}
