package ionutbalosin.training.ecommerce.shopping.cart.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
public interface IDao<T> {

  UUID save(T t);

  void saveAll(Collection<T> t);

  Optional<T> get(UUID cartItemId);

  List<T> getAll(UUID userId);

  int delete(UUID cartItemId);

  int deleteAll(UUID userId);

  int update(T t);
}
