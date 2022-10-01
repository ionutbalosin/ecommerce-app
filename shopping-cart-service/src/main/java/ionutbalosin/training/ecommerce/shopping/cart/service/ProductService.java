package ionutbalosin.training.ecommerce.shopping.cart.service;

import static com.google.common.collect.Iterators.partition;
import static ionutbalosin.training.ecommerce.shopping.cart.cache.ProductCache.CACHE_INSTANCE;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import ionutbalosin.training.ecommerce.shopping.cart.client.ProductClient;
import ionutbalosin.training.ecommerce.shopping.cart.model.ProductItem;
import ionutbalosin.training.ecommerce.shopping.cart.model.mapper.ProductItemMapper;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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

  private final ProductClient productClient;
  private final ProductItemMapper mapper;

  public ProductService(ProductClient productClient, ProductItemMapper mapper) {
    this.productClient = productClient;
    this.mapper = mapper;
  }

  public List<ProductItem> getProducts(Set<UUID> productIds) {
    final List<ProductItem> cachedProducts = CACHE_INSTANCE.getProducts(productIds);
    final Set<UUID> cachedProductIds =
        cachedProducts.stream().map(ProductItem::getProductId).collect(toSet());
    final Set<UUID> missedProductIds =
        productIds.stream().filter(not(cachedProductIds::contains)).collect(toSet());

    // Implement URL splitting (i.e., URL a has maximum length of 2048 characters)
    partition(missedProductIds.iterator(), 48)
        .forEachRemaining(
            ids -> {
              final List<ProductItem> retrievedProducts = getProductsFromService(ids);
              CACHE_INSTANCE.addProducts(retrievedProducts);
              cachedProducts.addAll(retrievedProducts);
            });

    return cachedProducts;
  }

  private List<ProductItem> getProductsFromService(List<UUID> productIds) {
    return productClient.productsGet(productIds).getBody().stream()
        .map(mapper::map)
        .collect(toList());
  }
}
