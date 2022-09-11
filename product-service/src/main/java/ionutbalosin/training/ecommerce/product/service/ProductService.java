package ionutbalosin.training.ecommerce.product.service;

import ionutbalosin.training.ecommerce.product.dao.ProductJdbcDao;
import ionutbalosin.training.ecommerce.product.exception.ResourceNotFoundException;
import ionutbalosin.training.ecommerce.product.model.entity.Product;
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

  public int updateProduct(Product product) {
    return productJdbcDao.update(product);
  }

  public UUID createProduct(Product product) {
    return productJdbcDao.save(product);
  }

  public int deleteProduct(UUID productId) {
    return productJdbcDao.delete(productId);
  }
}
