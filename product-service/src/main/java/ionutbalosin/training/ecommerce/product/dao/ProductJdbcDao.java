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
package ionutbalosin.training.ecommerce.product.dao;

import static ionutbalosin.training.ecommerce.product.util.DateUtil.localDateTimeToTimestamp;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

import ionutbalosin.training.ecommerce.product.dao.mapper.ProductRowMapper;
import ionutbalosin.training.ecommerce.product.model.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public class ProductJdbcDao implements IDao<Product> {

  private static final String SELECT_MAX_25_PRODUCTS =
      """
      SELECT * FROM PRODUCT
      TABLESAMPLE SYSTEM_ROWS(25) -- returns 25 random rows
      WHERE STAT = 'A';
      """;
  private static final String SELECT_PRODUCTS_BY_ID =
      """
      SELECT * FROM PRODUCT
      WHERE STAT = 'A' AND ID in (:ID)
      """;
  private static final String SELECT_PRODUCT =
      """
      SELECT * FROM PRODUCT
      WHERE ID = :ID AND STAT = 'A'
      """;
  private static final String UPSERT_PRODUCT =
      """
      INSERT INTO PRODUCT(NAME, BRAND, CATEGORY, PRICE, CURRENCY, QUANTITY, DAT_INS, USR_INS, STAT)
      VALUES (:NAME, :BRAND, :CATEGORY, :PRICE, :CURRENCY, :QUANTITY, :DAT_INS, :USR_INS, :STAT)
      ON CONFLICT (NAME, BRAND, CATEGORY) DO
      UPDATE
      SET PRICE = :PRICE,
          CURRENCY = :CURRENCY,
          QUANTITY = :QUANTITY,
          DAT_INS = :DAT_INS,
          USR_INS = :USR_INS
      RETURNING ID;
      """;
  private static final String UPDATE_PRODUCT =
      """
      UPDATE PRODUCT SET
      PRICE = COALESCE(:PRICE, PRICE),
      QUANTITY = COALESCE(:QUANTITY, QUANTITY),
      USR_UPD = :USR_UPD,
      DAT_UPD = :DAT_UPD
      WHERE ID = :ID;
      """;

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final ProductRowMapper rowMapper;

  public ProductJdbcDao(NamedParameterJdbcTemplate jdbcTemplate, ProductRowMapper rowMapper) {
    this.jdbcTemplate = jdbcTemplate;
    this.rowMapper = rowMapper;
  }

  @Override
  public Optional<Product> get(UUID productId) {
    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
    parameterSource.addValue(Product.ID, productId);
    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(SELECT_PRODUCT, parameterSource, rowMapper));
    } catch (EmptyResultDataAccessException erdae) {
      return Optional.empty();
    }
  }

  @Override
  public List<Product> getAll(List<UUID> productIds) {
    if (productIds == null || productIds.isEmpty()) {
      // TODO: Limited to 25 results (i.e., performance reasons)
      return jdbcTemplate.query(SELECT_MAX_25_PRODUCTS, rowMapper);
    }

    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
    parameterSource.addValue(Product.ID, productIds);
    return jdbcTemplate.query(SELECT_PRODUCTS_BY_ID, parameterSource, rowMapper);
  }

  @Override
  public UUID save(Product product) {
    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
    parameterSource.addValue(Product.NAME, product.getName());
    parameterSource.addValue(Product.BRAND, product.getBrand());
    parameterSource.addValue(Product.CATEGORY, product.getCategory());
    parameterSource.addValue(Product.PRICE, product.getPrice());
    parameterSource.addValue(Product.CURRENCY, product.getCurrency());
    parameterSource.addValue(Product.QUANTITY, product.getQuantity());
    parameterSource.addValue(Product.DAT_INS, localDateTimeToTimestamp(product.getDateIns()));
    parameterSource.addValue(Product.USR_INS, product.getUsrIns());
    parameterSource.addValue(Product.STAT, product.getStat());

    final KeyHolder holder = new GeneratedKeyHolder();
    jdbcTemplate.update(UPSERT_PRODUCT, parameterSource, holder);
    return (UUID) holder.getKeys().get(Product.ID);
  }

  @Override
  public int update(Product product) {
    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
    parameterSource.addValue(Product.ID, product.getId());
    parameterSource.addValue(Product.PRICE, product.getPrice());
    parameterSource.addValue(Product.QUANTITY, product.getQuantity());
    parameterSource.addValue(Product.DAT_UPD, localDateTimeToTimestamp(product.getDateUpd()));
    parameterSource.addValue(Product.USR_UPD, product.getUsrUpd());

    return jdbcTemplate.update(UPDATE_PRODUCT, parameterSource);
  }

  @Override
  public int delete(UUID productId) {
    throw new ResponseStatusException(NOT_IMPLEMENTED, "Unable to delete product id " + productId);
  }
}
