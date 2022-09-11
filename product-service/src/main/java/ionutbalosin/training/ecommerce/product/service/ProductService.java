package ionutbalosin.training.ecommerce.product.service;

import ionutbalosin.training.ecommerce.product.dao.ProductJdbcDao;
import ionutbalosin.training.ecommerce.product.entity.Product;
import ionutbalosin.training.ecommerce.product.exception.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private ProductJdbcDao productJdbcDao;

  public ProductService(ProductJdbcDao productJdbcDao) {
    this.productJdbcDao = productJdbcDao;
  }

  public Product getProduct(UUID productId) {
    return productJdbcDao.get(productId).orElseThrow(() -> new ResourceNotFoundException());
  }

  public List<Product> getProducts() {
    return productJdbcDao.getAll();
  }

  public void updateProduct(Product product) {
    productJdbcDao.update(product);
  }

  public UUID createProduct(Product product) {
    return productJdbcDao.save(product);
  }
}
