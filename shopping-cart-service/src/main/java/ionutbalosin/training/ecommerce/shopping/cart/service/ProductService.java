package ionutbalosin.training.ecommerce.shopping.cart.service;

import static com.google.common.collect.Iterators.partition;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import ionutbalosin.training.ecommerce.shopping.cart.client.ProductClient;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
@Service
public class ProductService {

  private ProductClient productClient;
  private Cache<UUID, ProductDto> productCache;

  public ProductService(ProductClient productClient) {
    this.productClient = productClient;
    this.productCache =
        Caffeine.newBuilder().maximumSize(1000).expireAfterWrite(1, TimeUnit.HOURS).build();
  }

  public List<ProductDto> getProducts(Set<UUID> productIds) {
    final List<ProductDto> cachedProducts = getProductsFromCache(productIds);
    final Set<UUID> cachedProductIds =
        cachedProducts.stream().map(ProductDto::getProductId).collect(toSet());
    final Set<UUID> missedProductIds =
        cachedProductIds.stream().filter(not(cachedProductIds::contains)).collect(toSet());

    // Implement URL splitting (i.e., URL a has maximum length of 2048 characters)
    partition(missedProductIds.iterator(), 48)
        .forEachRemaining(
            ids -> {
              final List<ProductDto> retrievedProducts = getProductsFromService(ids);
              addProductsToCache(retrievedProducts);
              cachedProducts.addAll(retrievedProducts);
            });

    return cachedProducts;
  }

  private List<ProductDto> getProductsFromCache(Set<UUID> productIds) {
    return productIds.stream()
        .map(productCache::getIfPresent)
        .filter(Objects::nonNull)
        .collect(toList());
  }

  private void addProductsToCache(List<ProductDto> products) {
    products.forEach(product -> productCache.put(product.getProductId(), product));
  }

  private List<ProductDto> getProductsFromService(List<UUID> productIds) {
    return productClient.productsGet(productIds).getBody();
  }
}
