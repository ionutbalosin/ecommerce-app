package ionutbalosin.training.ecommerce.order.service;

import ionutbalosin.training.ecommerce.order.dao.OrderJdbcDao;
import ionutbalosin.training.ecommerce.order.model.Order;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  private final OrderJdbcDao orderJdbcDao;

  public OrderService(OrderJdbcDao orderJdbcDao) {
    this.orderJdbcDao = orderJdbcDao;
  }

  public List<Order> getOrders(UUID userId) {
    return orderJdbcDao.getAll(userId);
  }

  public int updateOrder(Order order) {
    return orderJdbcDao.update(order);
  }

  public UUID createOrder(Order order) {
    return orderJdbcDao.save(order);
  }
}
