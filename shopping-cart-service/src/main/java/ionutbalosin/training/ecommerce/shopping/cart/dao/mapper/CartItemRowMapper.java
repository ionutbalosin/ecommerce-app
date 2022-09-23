package ionutbalosin.training.ecommerce.shopping.cart.dao.mapper;

import static ionutbalosin.training.ecommerce.shopping.cart.util.DateUtil.timestampToLocalDateTime;

import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;
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
public class CartItemRowMapper implements RowMapper<CartItem> {

  @Override
  public CartItem mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new CartItem()
        .id(UUID.fromString(rs.getString(CartItem.ID)))
        .productId(UUID.fromString(rs.getString(CartItem.PRODUCT_ID)))
        .quantity(rs.getInt(CartItem.QUANTITY))
        .discount(rs.getFloat(CartItem.DISCOUNT))
        .dateIns(timestampToLocalDateTime(rs.getTimestamp(CartItem.DAT_INS)))
        .usrIns(rs.getString(CartItem.USR_INS))
        .dateUpd(timestampToLocalDateTime(rs.getTimestamp(CartItem.DAT_UPD)))
        .usrUpd(rs.getString(CartItem.USR_UPD))
        .stat(rs.getString(CartItem.STAT));
  }
}
