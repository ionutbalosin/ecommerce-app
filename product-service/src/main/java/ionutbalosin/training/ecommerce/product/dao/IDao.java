package ionutbalosin.training.ecommerce.product.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDao<T> {

  Optional<T> get(UUID productId);

  List<T> getAll(List<UUID> productIds);

  UUID save(T t);

  int update(T t);

  int delete(UUID id);
}
