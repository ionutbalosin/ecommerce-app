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

public enum ProductCache {
  CACHE_INSTANCE;

  private Cache<UUID, ProductItem> productCache;

  ProductCache() {
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
