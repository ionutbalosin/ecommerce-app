package ionutbalosin.training.ecommerce.shopping.cart.cache;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import ionutbalosin.training.ecommerce.shopping.cart.model.ProductItem;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
@Component
public class ProductCache {

  private Cache<UUID, ProductItem> productCache;

  public ProductCache() {
    this.productCache =
        Caffeine.newBuilder().maximumSize(1000).expireAfterWrite(1, TimeUnit.HOURS).build();
  }

  public Optional<ProductItem> getProduct(UUID productId) {
    return ofNullable(productCache.getIfPresent(productId));
  }

  public List<ProductItem> getProducts(Set<UUID> productIds) {
    return productIds.stream()
        .map(productCache::getIfPresent)
        .filter(Objects::nonNull)
        .collect(toList());
  }

  public void addProduct(ProductItem product) {
    productCache.put(product.getProductId(), product);
  }

  public void addProducts(List<ProductItem> products) {
    products.forEach(product -> productCache.put(product.getProductId(), product));
  }
}
