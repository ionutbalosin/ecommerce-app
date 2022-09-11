package ionutbalosin.training.ecommerce.product.controller;

import static ionutbalosin.training.ecommerce.product.PostgresqlSingletonContainer.INSTANCE;
import static java.util.UUID.fromString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
class ProductControllerITest {

  private static UUID FAKE_UUID = fromString("00000000-0000-0000-0000-000000000000");
  private static UUID PREFILLED_UUID = fromString("330912c6-31d4-11ed-a261-0242ac120002");

  @Container private static final PostgreSQLContainer CONTAINER = INSTANCE.getContainer();

  @Autowired private MockMvc mockMvc;

  final ProductCreateDto PRODUCT =
      new ProductCreateDto()
          .name("Monkey Coffee")
          .brand("Zoo Land")
          .category("Beverage")
          .price(BigDecimal.valueOf(11.0))
          .currency(ProductCreateDto.CurrencyEnum.EUR)
          .quantity(999);

  @Test
  @Order(1)
  public void productsPost_isCreated() throws Exception {
    mockMvc
        .perform(
            post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(PRODUCT)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.productId", notNullValue()));
  }

  @Test
  @Order(2)
  public void productsGet_isOk() throws Exception {
    mockMvc
        .perform(get("/products").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[*].productId", notNullValue()))
        .andExpect(jsonPath("$[*].name", hasItem(PRODUCT.getName())))
        .andExpect(jsonPath("$[*].brand", hasItem(PRODUCT.getBrand())))
        .andExpect(jsonPath("$[*].category", hasItem(PRODUCT.getCategory())))
        .andExpect(jsonPath("$[*].price", hasItem(11.0)))
        .andExpect(jsonPath("$[*].currency", hasItem(PRODUCT.getCurrency().toString())))
        .andExpect(jsonPath("$[*].quantity", hasItem(PRODUCT.getQuantity())));
  }

  @Test
  @Order(3)
  public void productsProductIdGet_isOk() throws Exception {
    // this test relies on the prefilled DB data
    mockMvc
        .perform(
            get("/products/{productId}", PREFILLED_UUID).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productId", notNullValue()))
        .andExpect(jsonPath("$.name", is("Präsident Ganze Bohne")))
        .andExpect(jsonPath("$.brand", is("Julius Meinl")))
        .andExpect(jsonPath("$.category", is("Beverage")))
        .andExpect(jsonPath("$.price", is(5.0)))
        .andExpect(jsonPath("$.currency", is("EUR")))
        .andExpect(jsonPath("$.quantity", is(666)));
  }

  @Test
  @Order(4)
  public void productsProductIdPatch_isOk() throws Exception {
    // this test relies on the prefilled DB data
    final ProductUpdateDto productUpdate =
        new ProductUpdateDto().quantity(55).price(BigDecimal.valueOf(55.0));
    mockMvc
        .perform(
            patch("/products/{productId}", PREFILLED_UUID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(productUpdate)))
        .andExpect(status().isOk());
  }

  @Test
  @Order(5)
  public void productsProductIdGet_isNotFound() throws Exception {
    mockMvc
        .perform(get("/products/{productId}", FAKE_UUID).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  private String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
