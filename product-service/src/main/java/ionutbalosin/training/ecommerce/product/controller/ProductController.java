/**
 *  eCommerce Application
 *
 *  Copyright (c) 2022 - 2023 Ionut Balosin
 *  Website: www.ionutbalosin.com
 *  Twitter: @ionutbalosin / Mastodon: ionutbalosin@mastodon.socia
 *
 *
 *  MIT License
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */
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
