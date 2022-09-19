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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class ProductController implements ProductsApi {

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
    final Product product = entityMapper.map(productCreate);
    final UUID uuid = service.createProduct(product);
    return new ResponseEntity<>(dtoMapper.map(uuid), CREATED);
  }

  @Override
  public ResponseEntity<List<ProductDto>> productsGet(List<UUID> productIds) {
    final List<Product> products = service.getProducts(productIds);
    final List<ProductDto> productsDto = products.stream().map(dtoMapper::map).collect(toList());
    return new ResponseEntity<>(productsDto, OK);
  }

  @Override
  public ResponseEntity<ProductDto> productsProductIdGet(UUID productId) {
    final Product product = service.getProduct(productId);
    final ProductDto productDto = dtoMapper.map(product);
    return new ResponseEntity<>(productDto, OK);
  }

  @Override
  public ResponseEntity<Void> productsProductIdPatch(
      UUID productId, ProductUpdateDto productUpdateDto) {
    final Product product = entityMapper.map(productId, productUpdateDto);
    service.updateProduct(product);
    return new ResponseEntity<>(OK);
  }

  @Override
  public ResponseEntity<Void> productsProductIdDelete(UUID productId) {
    return new ResponseEntity<>(NOT_IMPLEMENTED);
  }
}
