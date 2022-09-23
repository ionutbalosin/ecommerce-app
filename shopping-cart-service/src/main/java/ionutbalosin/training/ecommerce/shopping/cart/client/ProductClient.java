package ionutbalosin.training.ecommerce.shopping.cart.client;

import io.swagger.v3.oas.annotations.Parameter;
import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
@FeignClient(name = "${product-service.name}", url = "${product-service-endpoint.url}")
public interface ProductClient {

  @RequestMapping(
      method = RequestMethod.GET,
      value = "/products",
      produces = {"application/json"})
  ResponseEntity<List<ProductDto>> productsGet(
      @Parameter(name = "productIds", description = "Product Ids")
          @Valid
          @RequestParam(value = "productIds", required = false)
          List<UUID> productIds);
}
