package ionutbalosin.training.ecommerce.shopping.cart.controller;

import static ionutbalosin.training.ecommerce.shopping.cart.PostgresqlSingletonContainer.INSTANCE;
import static ionutbalosin.training.ecommerce.shopping.cart.util.JsonUtil.asJsonString;
import static java.util.List.of;
import static java.util.UUID.fromString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemCreateDto;
import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemUpdateDto;
import ionutbalosin.training.ecommerce.shopping.cart.service.KafkaEventProducer;
import ionutbalosin.training.ecommerce.shopping.cart.service.ProductService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(properties = {"max.cart.items.per.request=3"})
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartControllerITest {

  private static final UUID USER_ID = fromString("42424242-4242-4242-4242-424242424242");
  private static final UUID FAKE_CART_ITEM_ID = fromString("00000000-0000-0000-0000-000000000000");

  @Container private static final PostgreSQLContainer CONTAINER = INSTANCE.getContainer();

  @Autowired private MockMvc mockMvc;
  @MockBean ProductService productService;
  @MockBean KafkaEventProducer kafkaEventProducer;

  final CartItemCreateDto CART_ITEM_1 =
      new CartItemCreateDto()
          .productId(UUID.fromString("8134fd12-3403-11ed-a261-0242ac120002"))
          .quantity(99)
          .discount(BigDecimal.valueOf(15.0));

  final CartItemCreateDto CART_ITEM_2 =
      new CartItemCreateDto()
          .productId(UUID.fromString("77359e48-3403-11ed-a261-0242ac120002"))
          .quantity(99)
          .discount(BigDecimal.valueOf(15.0));

  final CartItemUpdateDto CART_ITEM_UPDATE = new CartItemUpdateDto().quantity(99);

  @Test
  @Order(1)
  public void cartUserIdItemsPost_isCreated() throws Exception {
    mockMvc
        .perform(
            post("/cart/{userId}/items", USER_ID)
                .contentType(APPLICATION_JSON)
                .content(asJsonString(of(CART_ITEM_1, CART_ITEM_2))))
        .andExpect(status().isCreated());
  }

  @Test
  @Order(2)
  public void cartUserIdItemsPost_isForbidden() throws Exception {
    final List<CartItemCreateDto> cartItems =
        of(CART_ITEM_1, CART_ITEM_2, CART_ITEM_1, CART_ITEM_2);
    mockMvc
        .perform(
            post("/cart/{userId}/items", USER_ID)
                .contentType(APPLICATION_JSON)
                .content(asJsonString(cartItems)))
        .andExpect(status().isForbidden());
  }

  @Test
  @Order(3)
  public void cartUserIdItemsGet_isOk() throws Exception {
    mockMvc
        .perform(get("/cart/{userId}/items", USER_ID).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(2)))
        .andExpect(jsonPath("$[*].itemId", notNullValue()))
        .andExpect(
            jsonPath(
                "$[*].productId",
                hasItems(
                    CART_ITEM_1.getProductId().toString(), CART_ITEM_2.getProductId().toString())))
        .andExpect(
            jsonPath(
                "$[*].quantity", hasItems(CART_ITEM_1.getQuantity(), CART_ITEM_2.getQuantity())))
        .andExpect(
            jsonPath(
                "$[*].discount",
                hasItems(
                    CART_ITEM_1.getDiscount().doubleValue(),
                    CART_ITEM_2.getDiscount().doubleValue())));
  }

  @Test
  @Order(4)
  public void cartUserIdCheckoutPost() throws Exception {
    Mockito.when(productService.getProducts(any())).thenReturn(of());
    Mockito.doNothing().when(kafkaEventProducer).sendEvent(any());

    mockMvc
        .perform(post("/cart/{userId}/checkout", USER_ID).contentType(APPLICATION_JSON))
        .andExpect(status().isCreated());
  }

  @Test
  @Order(5)
  public void cartUserIdItemsDelete_isOk() throws Exception {
    mockMvc
        .perform(delete("/cart/{userId}/items", USER_ID).contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @Order(6)
  public void cartUserIdItemsGet_isOk_afterDelete() throws Exception {
    mockMvc
        .perform(get("/cart/{userId}/items", USER_ID).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(0)));
  }

  @Test
  @Order(7)
  public void cartUserIdCheckoutPost_notFound() throws Exception {
    mockMvc
        .perform(post("/cart/{userId}/checkout", USER_ID).contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @Order(8)
  public void cartUserIdItemsItemIdPut_isNotImplemented() throws Exception {
    mockMvc
        .perform(
            put("/cart/{userId}/items/{itemId}", USER_ID, FAKE_CART_ITEM_ID)
                .contentType(APPLICATION_JSON)
                .content(asJsonString(CART_ITEM_UPDATE)))
        .andExpect(status().isNotImplemented());
  }

  @Test
  @Order(9)
  public void cartUserIdItemsItemIdGet_isNotImplemented() throws Exception {
    mockMvc
        .perform(
            get("/cart/{userId}/items/{itemId}", USER_ID, FAKE_CART_ITEM_ID)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isNotImplemented());
  }

  @Test
  @Order(10)
  public void cartUserIdItemsItemIdDelete_isNotImplemented() throws Exception {
    mockMvc
        .perform(
            delete("/cart/{userId}/items/{itemId}", USER_ID, FAKE_CART_ITEM_ID)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isNotImplemented());
  }
}
