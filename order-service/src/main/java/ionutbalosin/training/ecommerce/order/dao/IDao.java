package ionutbalosin.training.ecommerce.order.dao;

import java.util.List;
import java.util.UUID;

public interface IDao<T> {

  List<T> getAll(UUID userId);

  UUID save(T t);

  int update(T t);
}
