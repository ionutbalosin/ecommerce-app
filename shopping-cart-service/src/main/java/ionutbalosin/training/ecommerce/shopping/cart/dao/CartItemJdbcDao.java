package ionutbalosin.training.ecommerce.shopping.cart.dao;

import static ionutbalosin.training.ecommerce.shopping.cart.util.DateUtil.localDateTimeToTimestamp;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

import ionutbalosin.training.ecommerce.shopping.cart.dao.mapper.CartItemRowMapper;
import ionutbalosin.training.ecommerce.shopping.cart.model.CartItem;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Repository
public class CartItemJdbcDao implements IDao<CartItem> {

  private static final String SELECT_ALL_CART_ITEMS =
      """
      SELECT * FROM CART_ITEM WHERE USER_ID = :USER_ID AND STAT = 'A'
    """;
  private static final String INSERT_CART_ITEM =
      """
      INSERT INTO CART_ITEM(USER_ID, PRODUCT_ID, DISCOUNT, QUANTITY, DAT_INS, USR_INS, STAT)
      VALUES (:USER_ID, :PRODUCT_ID, :DISCOUNT, :QUANTITY, :DAT_INS, :USR_INS, :STAT)
      RETURNING ID
    """;
  private static final String DELETE_CART_ITEMS =
      """
      UPDATE CART_ITEM SET
      STAT = 'D'
      WHERE USER_ID = :USER_ID
    """;

  private NamedParameterJdbcTemplate jdbcTemplate;
  private CartItemRowMapper rowMapper;

  public CartItemJdbcDao(NamedParameterJdbcTemplate jdbcTemplate, CartItemRowMapper rowMapper) {
    this.jdbcTemplate = jdbcTemplate;
    this.rowMapper = rowMapper;
  }

  @Override
  public UUID save(CartItem cartItem) {
    throw new ResponseStatusException(
        NOT_IMPLEMENTED, "Unable to save cart item by user id " + cartItem.getUserId());
  }

  @Override
  public void saveAll(Collection<CartItem> cartItems) {
    final SqlParameterSource[] batchArgs =
        cartItems.stream()
            .map(
                cartItem ->
                    new MapSqlParameterSource()
                        .addValue(CartItem.USER_ID, cartItem.getUserId())
                        .addValue(CartItem.PRODUCT_ID, cartItem.getProductId())
                        .addValue(CartItem.QUANTITY, cartItem.getQuantity())
                        .addValue(CartItem.DISCOUNT, cartItem.getDiscount())
                        .addValue(CartItem.DAT_INS, localDateTimeToTimestamp(cartItem.getDateIns()))
                        .addValue(CartItem.USR_INS, cartItem.getUsrIns())
                        .addValue(CartItem.STAT, cartItem.getStat()))
            .toArray(SqlParameterSource[]::new);

    jdbcTemplate.batchUpdate(INSERT_CART_ITEM, batchArgs);
  }

  @Override
  public Optional<CartItem> get(UUID id) {
    throw new ResponseStatusException(NOT_IMPLEMENTED, "Unable to get cart item by id " + id);
  }

  @Override
  public List<CartItem> getAll(UUID userId) {
    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
    parameterSource.addValue(CartItem.USER_ID, userId);
    return jdbcTemplate.query(SELECT_ALL_CART_ITEMS, parameterSource, rowMapper);
  }

  @Override
  public int delete(UUID id) {
    throw new ResponseStatusException(NOT_IMPLEMENTED, "Unable to delete cart item by id " + id);
  }

  @Override
  public int deleteAll(UUID userId) {
    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
    parameterSource.addValue(CartItem.USER_ID, userId);
    return jdbcTemplate.update(DELETE_CART_ITEMS, parameterSource);
  }

  @Override
  public int update(CartItem cartItem) {
    throw new ResponseStatusException(
        NOT_IMPLEMENTED, "Unable to update cart item by id " + cartItem.getId());
  }
}
