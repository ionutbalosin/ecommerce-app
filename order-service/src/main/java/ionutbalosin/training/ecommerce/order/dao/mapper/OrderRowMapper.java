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
package ionutbalosin.training.ecommerce.order.dao.mapper;

import static ionutbalosin.training.ecommerce.order.model.OrderStatus.fromValue;
import static ionutbalosin.training.ecommerce.order.util.DateUtil.timestampToLocalDateTime;
import static ionutbalosin.training.ecommerce.order.util.JsonUtil.stringToJsonObject;

import ionutbalosin.training.ecommerce.order.model.Order;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public class OrderRowMapper implements RowMapper<Order> {

  @Override
  public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new Order()
        .id(UUID.fromString(rs.getString(Order.ID)))
        .sourceEventId(UUID.fromString(rs.getString(Order.SOURCE_EVENT_ID)))
        .userId(UUID.fromString(rs.getString(Order.USER_ID)))
        .amount(rs.getDouble(Order.AMOUNT))
        .currency(rs.getString(Order.CURRENCY))
        .details(stringToJsonObject(rs.getString(Order.DETAILS)))
        .status(fromValue(rs.getString(Order.STATUS)))
        .dateIns(timestampToLocalDateTime(rs.getTimestamp(Order.DAT_INS)))
        .usrIns(rs.getString(Order.USR_INS))
        .dateUpd(timestampToLocalDateTime(rs.getTimestamp(Order.DAT_UPD)))
        .usrUpd(rs.getString(Order.USR_UPD))
        .stat(rs.getString(Order.STAT));
  }
}
