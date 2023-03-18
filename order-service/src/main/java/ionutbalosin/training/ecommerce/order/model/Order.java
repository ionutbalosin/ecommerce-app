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
package ionutbalosin.training.ecommerce.order.model;

import jakarta.json.JsonObject;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Order {

  public static final String ID = "ID";
  public static final String SOURCE_EVENT_ID = "SOURCE_EVENT_ID";
  public static final String USER_ID = "USER_ID";
  public static final String AMOUNT = "AMOUNT";
  public static final String CURRENCY = "CURRENCY";
  public static final String DETAILS = "DETAILS";
  public static final String STATUS = "STATUS";
  public static final String DAT_INS = "DAT_INS";
  public static final String DAT_UPD = "DAT_UPD";
  public static final String USR_INS = "USR_INS";
  public static final String USR_UPD = "USR_UPD";
  public static final String STAT = "STAT";

  private UUID id;
  private UUID sourceEventId;
  private UUID userId;
  private double amount;
  private String currency;
  private JsonObject details;
  private OrderStatus status;
  private LocalDateTime dateIns;
  private LocalDateTime dateUpd;
  private String usrIns;
  private String usrUpd;
  private String stat;

  public UUID getId() {
    return id;
  }

  public Order id(UUID id) {
    this.id = id;
    return this;
  }

  public UUID getSourceEventId() {
    return sourceEventId;
  }

  public Order sourceEventId(UUID sourceEventId) {
    this.sourceEventId = sourceEventId;
    return this;
  }

  public UUID getUserId() {
    return userId;
  }

  public Order userId(UUID userId) {
    this.userId = userId;
    return this;
  }

  public double getAmount() {
    return amount;
  }

  public Order amount(double amount) {
    this.amount = amount;
    return this;
  }

  public String getCurrency() {
    return currency;
  }

  public Order currency(String currency) {
    this.currency = currency;
    return this;
  }

  public JsonObject getDetails() {
    return details;
  }

  public Order details(JsonObject details) {
    this.details = details;
    return this;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public Order status(OrderStatus status) {
    this.status = status;
    return this;
  }

  public LocalDateTime getDateIns() {
    return dateIns;
  }

  public Order dateIns(LocalDateTime dateIns) {
    this.dateIns = dateIns;
    return this;
  }

  public LocalDateTime getDateUpd() {
    return dateUpd;
  }

  public Order dateUpd(LocalDateTime dateUpd) {
    this.dateUpd = dateUpd;
    return this;
  }

  public String getUsrIns() {
    return usrIns;
  }

  public Order usrIns(String usrIns) {
    this.usrIns = usrIns;
    return this;
  }

  public String getUsrUpd() {
    return usrUpd;
  }

  public Order usrUpd(String usrUpd) {
    this.usrUpd = usrUpd;
    return this;
  }

  public String getStat() {
    return stat;
  }

  public Order stat(String stat) {
    this.stat = stat;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Order order)) return false;
    return Objects.equals(sourceEventId, order.sourceEventId)
        && Objects.equals(userId, order.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceEventId, userId);
  }
}
