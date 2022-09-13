package ionutbalosin.training.ecommerce.product.controller;

import static ionutbalosin.training.ecommerce.product.PostgresqlSingletonContainer.INSTANCE;
import static ionutbalosin.training.ecommerce.product.util.JsonUtil.asJsonString;
import static java.util.UUID.fromString;
import static org.hamcrest.Matchers.hasItem;
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
import ionutbalosin.training.ecommerce.product.api.model.ProductUpdateDto;
import java.math.BigDecimal;
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
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerITest {

  private static UUID FAKE_UUID = fromString("00000000-0000-0000-0000-000000000000");
  private static UUID PREFILLED_UUID = fromString("330912c6-31d4-11ed-a261-0242ac120002");

  @Container private static final PostgreSQLContainer CONTAINER = INSTANCE.getContainer();

  @Autowired private MockMvc mockMvc;

  final ProductCreateDto PRODUCT_CREATE =
      new ProductCreateDto()
          .name("Monkey Coffee")
          .brand("Zoo Land")
          .category("Beverage")
          .price(BigDecimal.valueOf(99))
          .currency(ProductCreateDto.CurrencyEnum.EUR)
          .quantity(999);

  final ProductUpdateDto PRODUCT_UPDATE =
      new ProductUpdateDto().price(BigDecimal.valueOf(22)).quantity(222);

  @Test
  @Order(1)
  public void productsPost_isCreated() throws Exception {
    mockMvc
        .perform(
            post("/products").contentType(APPLICATION_JSON).content(asJsonString(PRODUCT_CREATE)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.productId", notNullValue()));
  }

  @Test
  @Order(2)
  public void productsGet_isOk() throws Exception {
    mockMvc
        .perform(get("/products").contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[*].productId", notNullValue()))
        .andExpect(jsonPath("$[*].name", hasItem(PRODUCT_CREATE.getName())))
        .andExpect(jsonPath("$[*].brand", hasItem(PRODUCT_CREATE.getBrand())))
        .andExpect(jsonPath("$[*].category", hasItem(PRODUCT_CREATE.getCategory())))
        .andExpect(jsonPath("$[*].price", hasItem(PRODUCT_CREATE.getPrice().doubleValue())))
        .andExpect(jsonPath("$[*].currency", hasItem(PRODUCT_CREATE.getCurrency().toString())))
        .andExpect(jsonPath("$[*].quantity", hasItem(PRODUCT_CREATE.getQuantity())));
  }

  @Test
  @Order(3)
  public void productsProductIdGet_isOk_prefilledDbProduct() throws Exception {
    // this test relies on the prefilled DB data
    mockMvc
        .perform(get("/products/{productId}", PREFILLED_UUID).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productId", notNullValue()))
        .andExpect(jsonPath("$.name", is("Präsident Ganze Bohne")))
        .andExpect(jsonPath("$.brand", is("Julius Meinl")))
        .andExpect(jsonPath("$.category", is("Beverage")))
        .andExpect(jsonPath("$.price", is(11.0)))
        .andExpect(jsonPath("$.currency", is("EUR")))
        .andExpect(jsonPath("$.quantity", is(111)));
  }

  @Test
  @Order(4)
  public void productsProductIdPatch_isOk() throws Exception {
    mockMvc
        .perform(
            patch("/products/{productId}", PREFILLED_UUID)
                .contentType(APPLICATION_JSON)
                .content(asJsonString(PRODUCT_UPDATE)))
        .andExpect(status().isOk());
  }

  @Test
  @Order(5)
  public void productsProductIdGet_isOk_updatedProduct() throws Exception {
    mockMvc
        .perform(get("/products/{productId}", PREFILLED_UUID).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productId", notNullValue()))
        .andExpect(jsonPath("$.price", is(PRODUCT_UPDATE.getPrice().doubleValue())))
        .andExpect(jsonPath("$.quantity", is(PRODUCT_UPDATE.getQuantity())));
  }

  @Test
  @Order(6)
  public void productsProductIdGet_isNotFound() throws Exception {
    mockMvc
        .perform(get("/products/{productId}", FAKE_UUID).contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(7)
  public void productsProductIdDelete_isNotImplemented() throws Exception {
    mockMvc
        .perform(delete("/products/{productId}", PREFILLED_UUID).contentType(APPLICATION_JSON))
        .andExpect(status().isNotImplemented());
  }
}
