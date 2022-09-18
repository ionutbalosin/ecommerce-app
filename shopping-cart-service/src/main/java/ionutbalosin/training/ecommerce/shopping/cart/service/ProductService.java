package ionutbalosin.training.ecommerce.shopping.cart.service;

import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import ionutbalosin.training.ecommerce.shopping.cart.client.ProductClient;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private ProductClient productClient;

  public ProductService(ProductClient productClient) {
    this.productClient = productClient;
  }

  public List<ProductDto> getProducts(Set<UUID> productIds) {
    final List<UUID> productIdsAsList = productIds.stream().collect(Collectors.toList());
    return productClient.productsGet(productIdsAsList).getBody();
  }
}
