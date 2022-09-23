package ionutbalosin.training.ecommerce.shopping.cart.service;

import static java.util.stream.Collectors.toList;

import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import ionutbalosin.training.ecommerce.shopping.cart.client.ProductClient;
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

  private ProductClient productClient;

  public ProductService(ProductClient productClient) {
    this.productClient = productClient;
  }

  public List<ProductDto> getProducts(Set<UUID> productIds) {
    final List<UUID> productIdsAsList = productIds.stream().collect(toList());
    return productClient.productsGet(productIdsAsList).getBody();
  }
}
