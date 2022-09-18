package ionutbalosin.training.ecommerce.product.service;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import ionutbalosin.training.ecommerce.product.dao.ProductJdbcDao;
import ionutbalosin.training.ecommerce.product.model.Product;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {

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

  public List<Product> getProducts(List<UUID> ids) {
    return productJdbcDao.getAll(ids);
  }

  public int updateProduct(Product product) {
    return productJdbcDao.update(product);
  }

  public UUID createProduct(Product product) {
    return productJdbcDao.save(product);
  }
}
