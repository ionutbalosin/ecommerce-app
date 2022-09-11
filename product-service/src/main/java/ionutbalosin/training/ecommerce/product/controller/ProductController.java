package ionutbalosin.training.ecommerce.product.controller;

import static java.util.stream.Collectors.toList;

import ionutbalosin.training.ecommerce.product.api.ProductsApi;
import ionutbalosin.training.ecommerce.product.api.model.ProductCreateDto;
import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import ionutbalosin.training.ecommerce.product.api.model.ProductIdDto;
import ionutbalosin.training.ecommerce.product.api.model.ProductUpdateDto;
import ionutbalosin.training.ecommerce.product.model.entity.Product;
import ionutbalosin.training.ecommerce.product.model.mapper.ProductDtoMapper;
import ionutbalosin.training.ecommerce.product.model.mapper.ProductEntityMapper;
import ionutbalosin.training.ecommerce.product.service.ProductService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class ProductController implements ProductsApi {

  private ProductDtoMapper dtoMapper;
  private ProductEntityMapper entityMapper;
  private ProductService service;

  public ProductController(
      ProductDtoMapper dtoMapper, ProductEntityMapper entityMapper, ProductService service) {
    this.dtoMapper = dtoMapper;
    this.entityMapper = entityMapper;
    this.service = service;
  }

  @Override
  public ResponseEntity<ProductIdDto> productsPost(ProductCreateDto productCreate) {
    final Product product = entityMapper.map(productCreate);
    final UUID uuid = service.createProduct(product);
    return new ResponseEntity<>(dtoMapper.map(uuid), HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<List<ProductDto>> productsGet() {
    final List<Product> product = service.getProducts();
    final List<ProductDto> productsDto = product.stream().map(dtoMapper::map).collect(toList());
    return new ResponseEntity<>(productsDto, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<ProductDto> productsProductIdGet(UUID productId) {
    final Product product = service.getProduct(productId);
    final ProductDto productDto = dtoMapper.map(product);
    return new ResponseEntity<>(productDto, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> productsProductIdPatch(
      UUID productId, ProductUpdateDto productUpdateDto) {
    final Product product = entityMapper.map(productId, productUpdateDto);
    service.updateProduct(product);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Override
  public ResponseEntity<Void> productsProductIdDelete(UUID productId) {
    service.deleteProduct(productId);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
