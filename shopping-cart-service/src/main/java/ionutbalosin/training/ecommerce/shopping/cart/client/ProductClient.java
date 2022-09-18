package ionutbalosin.training.ecommerce.shopping.cart.client;

import ionutbalosin.training.ecommerce.product.api.ProductsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "${product-service.name}", url = "${product-service-endpoint.url}")
public interface ProductClient extends ProductsApi {}
