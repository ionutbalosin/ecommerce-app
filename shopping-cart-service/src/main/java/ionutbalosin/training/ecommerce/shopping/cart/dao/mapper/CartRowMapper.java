package ionutbalosin.training.ecommerce.shopping.cart.dao.mapper;

import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static ionutbalosin.training.ecommerce.shopping.cart.util.DateUtil.timestampToLocalDateTime;

public class CartRowMapper implements RowMapper<CartItem> {

  @Override
  public CartItem mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new CartItem()
        .id(UUID.fromString(rs.getString(CartItem.ID)))
        .userId(UUID.fromString(rs.getString(CartItem.USER_ID)))
        .productId(UUID.fromString(rs.getString(CartItem.PRODUCT_ID)))
        .dateIns(timestampToLocalDateTime(rs.getTimestamp(CartItem.DAT_INS)))
        .usrIns(rs.getString(CartItem.USR_INS))
        .dateUpd(timestampToLocalDateTime(rs.getTimestamp(CartItem.DAT_UPD)))
        .usrUpd(rs.getString(CartItem.USR_UPD))
        .stat(rs.getString(CartItem.STAT));
  }
}
