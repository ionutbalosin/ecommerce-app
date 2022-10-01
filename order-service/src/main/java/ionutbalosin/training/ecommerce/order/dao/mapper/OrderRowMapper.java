package ionutbalosin.training.ecommerce.order.dao.mapper;

import static ionutbalosin.training.ecommerce.order.model.OrderStatus.fromValue;
import static ionutbalosin.training.ecommerce.order.util.DateUtil.timestampToLocalDateTime;
import static ionutbalosin.training.ecommerce.order.util.JsonUtil.stringToJsonObject;

import ionutbalosin.training.ecommerce.order.model.Order;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
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
