package ionutbalosin.training.ecommerce.shopping.cart.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDao<T> {

  UUID save(T t);

  void saveAll(Collection<T> t);

  Optional<T> get(UUID id);

  List<T> getAll(UUID id);

  int delete(UUID id);

  int deleteAll(UUID id);

  int update(T t);
}
