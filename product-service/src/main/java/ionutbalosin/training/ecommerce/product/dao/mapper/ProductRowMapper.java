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
package ionutbalosin.training.ecommerce.product.dao.mapper;

import static ionutbalosin.training.ecommerce.product.util.DateUtil.timestampToLocalDateTime;

import ionutbalosin.training.ecommerce.product.model.Product;
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
        .price(rs.getDouble(Product.PRICE))
        .currency(rs.getString(Product.CURRENCY))
        .quantity(rs.getInt(Product.QUANTITY))
        .dateIns(timestampToLocalDateTime(rs.getTimestamp(Product.DAT_INS)))
        .usrIns(rs.getString(Product.USR_INS))
        .dateUpd(timestampToLocalDateTime(rs.getTimestamp(Product.DAT_UPD)))
        .usrUpd(rs.getString(Product.USR_UPD))
        .stat(rs.getString(Product.STAT));
  }
}
