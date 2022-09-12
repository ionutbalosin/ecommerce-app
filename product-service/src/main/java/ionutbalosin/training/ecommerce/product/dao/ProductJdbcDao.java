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

  private static final String SELECT_ALL_PRODUCTS = "SELECT * FROM PRODUCT WHERE STAT = 'A'";
  private static final String SELECT_PRODUCT =
      "SELECT * FROM PRODUCT WHERE ID = :ID AND STAT = 'A'";
  private static final String INSERT_PRODUCT =
      """
        INSERT INTO PRODUCT(NAME, BRAND, CATEGORY, PRICE, CURRENCY, QUANTITY, DAT_INS, USR_INS, STAT)
        VALUES (:NAME, :BRAND, :CATEGORY, :PRICE, :CURRENCY, :QUANTITY, :DAT_INS, :USR_INS, :STAT)
        RETURNING ID;
      """;
  private static final String UPDATE_PRODUCT =
      """
        UPDATE PRODUCT SET
        PRICE = COALESCE(:PRICE, PRICE),
        QUANTITY = COALESCE(:QUANTITY, QUANTITY),
        USR_UPD = :USR_UPD,
        DAT_UPD = :DAT_UPD
        WHERE ID = :ID
      """;

  private NamedParameterJdbcTemplate jdbcTemplate;
  private ProductRowMapper rowMapper;

  public ProductJdbcDao(NamedParameterJdbcTemplate jdbcTemplate, ProductRowMapper rowMapper) {
    this.jdbcTemplate = jdbcTemplate;
    this.rowMapper = rowMapper;
  }

  @Override
  public Optional<Product> get(UUID id) {
    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
    parameterSource.addValue(Product.ID, id);
    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(SELECT_PRODUCT, parameterSource, rowMapper));
    } catch (EmptyResultDataAccessException erdae) {
      return Optional.empty();
    }
  }

  @Override
  public List<Product> getAll() {
    return jdbcTemplate.query(SELECT_ALL_PRODUCTS, rowMapper);
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

    KeyHolder holder = new GeneratedKeyHolder();
    jdbcTemplate.update(INSERT_PRODUCT, parameterSource, holder);
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
  public int delete(UUID id) {
    throw new ResponseStatusException(NOT_IMPLEMENTED, "Unable to delete product id " + id);
  }
}
