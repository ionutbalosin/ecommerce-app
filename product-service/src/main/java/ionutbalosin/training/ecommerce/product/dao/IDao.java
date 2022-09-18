package ionutbalosin.training.ecommerce.product.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDao<T> {

  Optional<T> get(UUID id);

  List<T> getAll(List<UUID> ids);

  UUID save(T t);

  int update(T t);

  int delete(UUID id);
}
