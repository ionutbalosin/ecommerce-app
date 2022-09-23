package ionutbalosin.training.ecommerce.order.dao;

import static ionutbalosin.training.ecommerce.order.util.DateUtil.localDateTimeToTimestamp;
import static ionutbalosin.training.ecommerce.order.util.JsonUtil.jsonObjectToString;

import ionutbalosin.training.ecommerce.order.dao.mapper.OrderRowMapper;
import ionutbalosin.training.ecommerce.order.model.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
@Repository
public class OrderJdbcDao implements IDao<Order> {

  private static final String SELECT_ORDER_BY_ID =
      """
        SELECT * FROM ORDERS
        WHERE STAT = 'A' AND ID = :ID;
        """;

  private static final String SELECT_ORDERS_BY_USER_ID =
      """
      SELECT * FROM ORDERS
      WHERE STAT = 'A' AND USER_ID = :USER_ID;
      """;

  private static final String UPSERT_ORDER =
      """
      INSERT INTO ORDERS(SOURCE_EVENT_ID, USER_ID, AMOUNT, CURRENCY, DETAILS, STATUS, DAT_INS, USR_INS, STAT)
      VALUES (:SOURCE_EVENT_ID, :USER_ID, :AMOUNT, :CURRENCY, (:DETAILS)::json, :STATUS, :DAT_INS, :USR_INS, :STAT)
      ON CONFLICT (SOURCE_EVENT_ID, USER_ID) DO NOTHING
      RETURNING ID;
      """;
  private static final String UPDATE_ORDER =
      """
      UPDATE ORDERS SET
      STATUS = COALESCE(:STATUS, STATUS),
      USR_UPD = :USR_UPD,
      DAT_UPD = :DAT_UPD
      WHERE ID = :ID;
      """;

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final OrderRowMapper rowMapper;

  public OrderJdbcDao(NamedParameterJdbcTemplate jdbcTemplate, OrderRowMapper rowMapper) {
    this.jdbcTemplate = jdbcTemplate;
    this.rowMapper = rowMapper;
  }

  @Override
  public Optional<Order> get(UUID orderId) {
    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
    parameterSource.addValue(Order.ID, orderId);
    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(SELECT_ORDER_BY_ID, parameterSource, rowMapper));
    } catch (EmptyResultDataAccessException erdae) {
      return Optional.empty();
    }
  }

  @Override
  public List<Order> getAll(UUID userId) {
    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
    parameterSource.addValue(Order.USER_ID, userId);
    return jdbcTemplate.query(SELECT_ORDERS_BY_USER_ID, parameterSource, rowMapper);
  }

  @Override
  public UUID save(Order order) {
    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
    parameterSource.addValue(Order.SOURCE_EVENT_ID, order.getSourceEventId());
    parameterSource.addValue(Order.USER_ID, order.getUserId());
    parameterSource.addValue(Order.AMOUNT, order.getAmount());
    parameterSource.addValue(Order.CURRENCY, order.getCurrency());
    parameterSource.addValue(Order.DETAILS, jsonObjectToString(order.getDetails()));
    parameterSource.addValue(Order.STATUS, order.getStatus().getValue());
    parameterSource.addValue(Order.DAT_INS, localDateTimeToTimestamp(order.getDateIns()));
    parameterSource.addValue(Order.USR_INS, order.getUsrIns());
    parameterSource.addValue(Order.STAT, order.getStat());

    final KeyHolder holder = new GeneratedKeyHolder();
    jdbcTemplate.update(UPSERT_ORDER, parameterSource, holder);
    return (UUID) holder.getKeys().get(Order.ID);
  }

  @Override
  public int update(Order order) {
    MapSqlParameterSource parameterSource = new MapSqlParameterSource();
    parameterSource.addValue(Order.ID, order.getId());
    parameterSource.addValue(Order.STATUS, order.getStatus().getValue());
    parameterSource.addValue(Order.DAT_UPD, localDateTimeToTimestamp(order.getDateUpd()));
    parameterSource.addValue(Order.USR_UPD, order.getUsrUpd());

    return jdbcTemplate.update(UPDATE_ORDER, parameterSource);
  }
}
