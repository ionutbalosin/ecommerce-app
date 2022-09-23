package ionutbalosin.training.ecommerce.product.controller;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static org.springframework.http.HttpStatus.OK;

import ionutbalosin.training.ecommerce.product.api.ProductsApi;
import ionutbalosin.training.ecommerce.product.api.model.ProductCreateDto;
import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import ionutbalosin.training.ecommerce.product.api.model.ProductIdDto;
import ionutbalosin.training.ecommerce.product.api.model.ProductUpdateDto;
import ionutbalosin.training.ecommerce.product.dto.mapper.ProductDtoMapper;
import ionutbalosin.training.ecommerce.product.model.Product;
import ionutbalosin.training.ecommerce.product.model.mapper.ProductMapper;
import ionutbalosin.training.ecommerce.product.service.ProductService;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
@Controller
public class ProductController implements ProductsApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

  private final ProductDtoMapper dtoMapper;
  private final ProductMapper entityMapper;
  private final ProductService service;

  public ProductController(
      ProductDtoMapper dtoMapper, ProductMapper entityMapper, ProductService service) {
    this.dtoMapper = dtoMapper;
    this.entityMapper = entityMapper;
    this.service = service;
  }

  @Override
  public ResponseEntity<ProductIdDto> productsPost(ProductCreateDto productCreate) {
    LOGGER.debug(
        "productsPost(name = '{}', brand = '{}', quantity = '{}', price = '{}')",
        productCreate.getName(),
        productCreate.getBrand(),
        productCreate.getQuantity(),
        productCreate.getPrice());
    final Product product = entityMapper.map(productCreate);
    final UUID uuid = service.createProduct(product);
    return new ResponseEntity<>(dtoMapper.map(uuid), CREATED);
  }

  @Override
  public ResponseEntity<List<ProductDto>> productsGet(List<UUID> productIds) {
    LOGGER.debug("productsGet(productIds = '{}')", productIds);
    final List<Product> products = service.getProducts(productIds);
    final List<ProductDto> productsDto = products.stream().map(dtoMapper::map).collect(toList());
    return new ResponseEntity<>(productsDto, OK);
  }

  @Override
  public ResponseEntity<ProductDto> productsProductIdGet(UUID productId) {
    LOGGER.debug("productsProductIdGet(productId = '{}')", productId);
    final Product product = service.getProduct(productId);
    final ProductDto productDto = dtoMapper.map(product);
    return new ResponseEntity<>(productDto, OK);
  }

  @Override
  public ResponseEntity<Void> productsProductIdPatch(
      UUID productId, ProductUpdateDto productUpdate) {
    LOGGER.debug(
        "productsProductIdPatch(productId = '{}', quantity = '{}', price = '{}')",
        productId,
        productUpdate.getQuantity(),
        productUpdate.getPrice());
    final Product product = entityMapper.map(productId, productUpdate);
    service.updateProduct(product);
    return new ResponseEntity<>(OK);
  }

  @Override
  public ResponseEntity<Void> productsProductIdDelete(UUID productId) {
    LOGGER.debug("productsProductIdDelete(productId = '{}')", productId);
    return new ResponseEntity<>(NOT_IMPLEMENTED);
  }
}
