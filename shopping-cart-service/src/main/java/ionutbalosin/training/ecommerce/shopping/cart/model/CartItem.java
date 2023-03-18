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

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class CartItem {

  public static final String ID = "ID";
  public static final String USER_ID = "USER_ID";
  public static final String PRODUCT_ID = "PRODUCT_ID";
  public static final String QUANTITY = "QUANTITY";
  public static final String DISCOUNT = "DISCOUNT";
  public static final String DAT_INS = "DAT_INS";
  public static final String DAT_UPD = "DAT_UPD";
  public static final String USR_INS = "USR_INS";
  public static final String USR_UPD = "USR_UPD";
  public static final String STAT = "STAT";

  private UUID id;
  private UUID userId;
  private UUID productId;
  private Integer quantity;
  private double discount;
  private LocalDateTime dateIns;
  private LocalDateTime dateUpd;
  private String usrIns;
  private String usrUpd;
  private String stat;

  public UUID getId() {
    return id;
  }

  public CartItem id(UUID id) {
    this.id = id;
    return this;
  }

  public UUID getUserId() {
    return userId;
  }

  public CartItem userId(UUID userId) {
    this.userId = userId;
    return this;
  }

  public UUID getProductId() {
    return productId;
  }

  public CartItem productId(UUID productId) {
    this.productId = productId;
    return this;
  }

  public double getDiscount() {
    return discount;
  }

  public CartItem discount(double discount) {
    this.discount = discount;
    return this;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public CartItem quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  public LocalDateTime getDateIns() {
    return dateIns;
  }

  public CartItem dateIns(LocalDateTime dateIns) {
    this.dateIns = dateIns;
    return this;
  }

  public LocalDateTime getDateUpd() {
    return dateUpd;
  }

  public CartItem dateUpd(LocalDateTime dateUpd) {
    this.dateUpd = dateUpd;
    return this;
  }

  public String getUsrIns() {
    return usrIns;
  }

  public CartItem usrIns(String usrIns) {
    this.usrIns = usrIns;
    return this;
  }

  public String getUsrUpd() {
    return usrUpd;
  }

  public CartItem usrUpd(String usrUpd) {
    this.usrUpd = usrUpd;
    return this;
  }

  public String getStat() {
    return stat;
  }

  public CartItem stat(String stat) {
    this.stat = stat;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CartItem cartItem)) return false;
    return userId.equals(cartItem.userId) && productId.equals(cartItem.productId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, productId);
  }
}
