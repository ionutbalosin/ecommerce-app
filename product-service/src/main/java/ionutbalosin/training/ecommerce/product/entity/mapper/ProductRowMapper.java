package ionutbalosin.training.ecommerce.product.entity.mapper;

import static ionutbalosin.training.ecommerce.product.util.DateUtil.timestampToLocalDateTime;

import ionutbalosin.training.ecommerce.product.entity.Product;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public class ProductRowMapper implements RowMapper<Product> {

  @Override
  public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
    return new Product()
        .id(UUID.fromString(rs.getString(Product.ID)))
        .name(rs.getString(Product.NAME))
        .brand(rs.getString(Product.BRAND))
        .category(rs.getString(Product.CATEGORY))
        .price(rs.getFloat(Product.PRICE))
        .currency(rs.getString(Product.CURRENCY))
        .quantity(rs.getInt(Product.QUANTITY))
        .dateIns(timestampToLocalDateTime(rs.getTimestamp(Product.DAT_INS)))
        .usrIns(rs.getString(Product.USR_INS))
        .dateUpd(timestampToLocalDateTime(rs.getTimestamp(Product.DAT_UPD)))
        .usrUpd(rs.getString(Product.USR_UPD))
        .stat(rs.getString(Product.STAT));
  }
}
