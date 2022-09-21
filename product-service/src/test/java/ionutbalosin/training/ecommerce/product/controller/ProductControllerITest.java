package ionutbalosin.training.ecommerce.product.controller;

import static ionutbalosin.training.ecommerce.product.PostgresqlSingletonContainer.INSTANCE;
import static ionutbalosin.training.ecommerce.product.util.JsonUtil.asJsonString;
import static java.math.BigDecimal.valueOf;
import static java.util.UUID.fromString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
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
          .price(valueOf(99))
          .currency(ProductCreateDto.CurrencyEnum.EUR)
          .quantity(999);

  final ProductDto PREFILLED_PRODUCT =
      new ProductDto()
          .productId(fromString("330912c6-31d4-11ed-a261-0242ac120002"))
          .name("Präsident Ganze Bohne")
          .brand("Julius Meinl")
          .category("Beverage")
          .price(valueOf(11.0))
          .currency(ProductDto.CurrencyEnum.EUR)
          .quantity(111);

  final ProductUpdateDto PRODUCT_UPDATE = new ProductUpdateDto().price(valueOf(22)).quantity(222);

  @Test
  @Order(1)
  public void productsGet_isOk_prefilledData() throws Exception {
    // this test relies on the prefilled DB data
    mockMvc
        .perform(
            get("/products")
                .queryParam("productId", PREFILLED_PRODUCT.getProductId().toString())
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].productId", is(PREFILLED_PRODUCT.getProductId().toString())))
        .andExpect(jsonPath("$[0].name", is(PREFILLED_PRODUCT.getName())))
        .andExpect(jsonPath("$[0].brand", is(PREFILLED_PRODUCT.getBrand())))
        .andExpect(jsonPath("$[0].category", is(PREFILLED_PRODUCT.getCategory())))
        .andExpect(jsonPath("$[0].price", is(PREFILLED_PRODUCT.getPrice().doubleValue())))
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
        .andExpect(jsonPath("$.price", is(PREFILLED_PRODUCT.getPrice().doubleValue())))
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
        .andExpect(jsonPath("$.*", hasSize(2)))
        .andExpect(jsonPath("$[*].productId", hasItem(PREFILLED_PRODUCT.getProductId().toString())))
        .andExpect(jsonPath("$[*].name", hasItems(PREFILLED_PRODUCT.getName(), PRODUCT.getName())))
        .andExpect(
            jsonPath("$[*].brand", hasItems(PREFILLED_PRODUCT.getBrand(), PRODUCT.getBrand())))
        .andExpect(
            jsonPath(
                "$[*].category", hasItems(PREFILLED_PRODUCT.getCategory(), PRODUCT.getCategory())))
        .andExpect(
            jsonPath(
                "$[*].price",
                hasItems(
                    PREFILLED_PRODUCT.getPrice().doubleValue(), PRODUCT.getPrice().doubleValue())))
        .andExpect(
            jsonPath(
                "$[*].currency",
                hasItems(
                    PREFILLED_PRODUCT.getCurrency().toString(), PRODUCT.getCurrency().toString())))
        .andExpect(
            jsonPath(
                "$[*].quantity", hasItems(PREFILLED_PRODUCT.getQuantity(), PRODUCT.getQuantity())));
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
        .andExpect(jsonPath("$.price", is(PRODUCT_UPDATE.getPrice().doubleValue())))
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
