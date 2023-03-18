/**
 *  eCommerce Application
 *
 *  Copyright (c) 2022 - 2023 Ionut Balosin
 *  Website: www.ionutbalosin.com
 *  Twitter: @ionutbalosin / Mastodon: ionutbalosin@mastodon.socia
 *
 *
 *  MIT License
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */
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
