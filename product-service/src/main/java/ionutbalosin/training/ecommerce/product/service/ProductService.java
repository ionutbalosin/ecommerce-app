package ionutbalosin.training.ecommerce.product.service;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import ionutbalosin.training.ecommerce.product.dao.ProductJdbcDao;
import ionutbalosin.training.ecommerce.product.model.Product;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
@Service
public class ProductService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

  public static final String BULK_HEAD_NAME = "semaphoreBulkhead";

  private final ProductJdbcDao productJdbcDao;

  public ProductService(ProductJdbcDao productJdbcDao) {
    this.productJdbcDao = productJdbcDao;
  }

  public Product getProduct(UUID productId) {
    return productJdbcDao
        .get(productId)
        .orElseThrow(
            () -> new ResponseStatusException(NOT_FOUND, "Not found product id " + productId));
  }

  @Bulkhead(name = BULK_HEAD_NAME, fallbackMethod = "fallback")
  public List<Product> getProducts(List<UUID> productIds) {
    return productJdbcDao.getAll(productIds);
  }

  public int updateProduct(Product product) {
    return productJdbcDao.update(product);
  }

  public UUID createProduct(Product product) {
    return productJdbcDao.save(product);
  }

  public List<Product> fallback(Exception e) {
    LOGGER.error("Product retrieval fallback method. Error encountered = '{}'", e.getMessage());
    return List.of();
  }
}
