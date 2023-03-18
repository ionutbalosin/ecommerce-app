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

import static ionutbalosin.training.ecommerce.product.PostgresqlSingletonContainer.INSTANCE;
import static ionutbalosin.training.ecommerce.product.util.JsonUtil.asJsonString;
import static java.util.UUID.fromString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ionutbalosin.training.ecommerce.product.api.model.ProductCreateDto;
import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import ionutbalosin.training.ecommerce.product.api.model.ProductUpdateDto;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerITest {

  private final UUID FAKE_PRODUCT_UUID = fromString("00000000-0000-0000-0000-000000000000");

  @Container
  private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER = INSTANCE.getContainer();

  @Autowired private MockMvc mockMvc;

  final ProductCreateDto PRODUCT =
      new ProductCreateDto()
          .name("Monkey Coffee")
          .brand("Zoo Land")
          .category("Beverage")
          .price(99.0)
          .currency(ProductCreateDto.CurrencyEnum.EUR)
          .quantity(999);

  final ProductDto PREFILLED_PRODUCT =
      new ProductDto()
          .productId(fromString("330912c6-31d4-11ed-a261-0242ac120002"))
          .name("Präsident Ganze Bohne")
          .brand("Julius Meinl")
          .category("Beverage")
          .price(11.0)
          .currency(ProductDto.CurrencyEnum.EUR)
          .quantity(111);

  final ProductUpdateDto PRODUCT_UPDATE = new ProductUpdateDto().price(22.0).quantity(222);

  @Test
  @Order(1)
  public void productsGet_isOk_prefilledData() throws Exception {
    // this test relies on the prefilled DB data
    mockMvc
        .perform(
            get("/products")
                .queryParam("productIds", PREFILLED_PRODUCT.getProductId().toString())
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].productId", is(PREFILLED_PRODUCT.getProductId().toString())))
        .andExpect(jsonPath("$[0].name", is(PREFILLED_PRODUCT.getName())))
        .andExpect(jsonPath("$[0].brand", is(PREFILLED_PRODUCT.getBrand())))
        .andExpect(jsonPath("$[0].category", is(PREFILLED_PRODUCT.getCategory())))
        .andExpect(jsonPath("$[0].price", is(PREFILLED_PRODUCT.getPrice())))
        .andExpect(jsonPath("$[0].currency", is(PREFILLED_PRODUCT.getCurrency().getValue())))
        .andExpect(jsonPath("$[0].quantity", is(PREFILLED_PRODUCT.getQuantity())));
  }

  @Test
  @Order(2)
  public void productsProductIdGet_isOk_prefilledData() throws Exception {
    // this test relies on the prefilled DB data
    mockMvc
        .perform(
            get("/products/{productId}", PREFILLED_PRODUCT.getProductId())
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productId", is(PREFILLED_PRODUCT.getProductId().toString())))
        .andExpect(jsonPath("$.name", is(PREFILLED_PRODUCT.getName())))
        .andExpect(jsonPath("$.brand", is(PREFILLED_PRODUCT.getBrand())))
        .andExpect(jsonPath("$.category", is(PREFILLED_PRODUCT.getCategory())))
        .andExpect(jsonPath("$.price", is(PREFILLED_PRODUCT.getPrice())))
        .andExpect(jsonPath("$.currency", is(PREFILLED_PRODUCT.getCurrency().getValue())))
        .andExpect(jsonPath("$.quantity", is(PREFILLED_PRODUCT.getQuantity())));
  }

  @Test
  @Order(3)
  public void productsPost_isCreated() throws Exception {
    mockMvc
        .perform(post("/products").contentType(APPLICATION_JSON).content(asJsonString(PRODUCT)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.productId", notNullValue()));
  }

  @Test
  @Order(4)
  public void productsGet_isOk() throws Exception {
    mockMvc
        .perform(get("/products").contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(25))); // DB limitation
  }

  @Test
  @Order(5)
  public void productsProductIdPatch_isOk() throws Exception {
    mockMvc
        .perform(
            patch("/products/{productId}", PREFILLED_PRODUCT.getProductId())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(PRODUCT_UPDATE)))
        .andExpect(status().isOk());
  }

  @Test
  @Order(6)
  public void productsProductIdGet_isOk_updatedProduct() throws Exception {
    mockMvc
        .perform(
            get("/products/{productId}", PREFILLED_PRODUCT.getProductId())
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productId", notNullValue()))
        .andExpect(jsonPath("$.price", is(PRODUCT_UPDATE.getPrice())))
        .andExpect(jsonPath("$.quantity", is(PRODUCT_UPDATE.getQuantity())));
  }

  @Test
  @Order(7)
  public void productsProductIdGet_isNotFound() throws Exception {
    mockMvc
        .perform(get("/products/{productId}", FAKE_PRODUCT_UUID).contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(8)
  public void productsProductIdDelete_isNotImplemented() throws Exception {
    mockMvc
        .perform(
            delete("/products/{productId}", PREFILLED_PRODUCT.getProductId())
                .contentType(APPLICATION_JSON))
        .andExpect(status().isNotImplemented());
  }
}
