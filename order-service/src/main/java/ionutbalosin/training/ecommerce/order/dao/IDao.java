package ionutbalosin.training.ecommerce.order.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDao<T> {

  Optional<T> get(UUID orderId);

  List<T> getAll(UUID userId);

  UUID save(T t);

  int update(T t);
}
