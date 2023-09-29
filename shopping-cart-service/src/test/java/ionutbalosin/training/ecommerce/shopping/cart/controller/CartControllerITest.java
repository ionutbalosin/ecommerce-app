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
package ionutbalosin.training.ecommerce.shopping.cart.controller;

import static ionutbalosin.training.ecommerce.message.schema.currency.Currency.EUR;
import static ionutbalosin.training.ecommerce.shopping.cart.KafkaContainerConfiguration.consumerConfigs;
import static ionutbalosin.training.ecommerce.shopping.cart.listener.OrderEventListener.ORDERS_TOPIC;
import static ionutbalosin.training.ecommerce.shopping.cart.util.JsonUtil.asJsonString;
import static java.util.List.of;
import static java.util.UUID.fromString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import ionutbalosin.training.ecommerce.message.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.shopping.cart.KafkaContainerConfiguration;
import ionutbalosin.training.ecommerce.shopping.cart.KafkaSingletonContainer;
import ionutbalosin.training.ecommerce.shopping.cart.PostgresqlSingletonContainer;
import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemCreateDto;
import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemUpdateDto;
import ionutbalosin.training.ecommerce.shopping.cart.model.ProductItem;
import ionutbalosin.training.ecommerce.shopping.cart.service.ProductService;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest(
    properties = {
      "max.cart.items.per.request=3",
      "product-service.name=localhost",
      "product-service-endpoint.url=http://localhost:8080"
    })
@AutoConfigureMockMvc
@Import(KafkaContainerConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartControllerITest {

  private final UUID USER_ID = fromString("42424242-4242-4242-4242-424242424242");
  private final UUID FAKE_CART_ITEM_ID = fromString("00000000-0000-0000-0000-000000000000");

  private final ProductItem PRODUCT_1 =
      new ProductItem()
          .productId(UUID.fromString("8134fd12-3403-11ed-a261-0242ac120002"))
          .name("Monkey Coffee")
          .brand("Zoo Land")
          .category("Beverage")
          .price(11.0)
          .currency(ProductItem.CurrencyEnum.EUR)
          .quantity(111);

  private final ProductItem PRODUCT_2 =
      new ProductItem()
          .productId(UUID.fromString("77359e48-3403-11ed-a261-0242ac120002"))
          .name("Tiger Coffee")
          .brand("Wonder Land")
          .category("Beverage")
          .price(22.0)
          .currency(ProductItem.CurrencyEnum.EUR)
          .quantity(222);

  private final CartItemCreateDto CART_ITEM_1 =
      new CartItemCreateDto().productId(PRODUCT_1.getProductId()).quantity(1).discount(10.0);

  private final CartItemCreateDto CART_ITEM_2 =
      new CartItemCreateDto().productId(PRODUCT_2.getProductId()).quantity(2).discount(20.0);

  private final CartItemUpdateDto CART_ITEM_UPDATE = new CartItemUpdateDto().quantity(3);

  @Container
  private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER =
      PostgresqlSingletonContainer.INSTANCE.getContainer();

  @Container
  private static final KafkaContainer KAFKA_CONTAINER =
      KafkaSingletonContainer.INSTANCE.getContainer();

  @Autowired private MockMvc mockMvc;
  @MockBean private ProductService productService;

  @BeforeAll
  public static void setUp() {
    KafkaSingletonContainer.INSTANCE.start();
  }

  @AfterAll
  public static void tearDown() {
    KafkaSingletonContainer.INSTANCE.stop();
  }

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
                "$[*].discount", hasItems(CART_ITEM_1.getDiscount(), CART_ITEM_2.getDiscount())));
  }

  @Test
  @Order(4)
  public void cartUserIdCheckoutPost() throws Exception {
    final KafkaConsumer<String, OrderCreatedEvent> kafkaConsumer =
        new KafkaConsumer(consumerConfigs());
    kafkaConsumer.subscribe(of(ORDERS_TOPIC));

    when(productService.getProducts(any())).thenReturn(of(PRODUCT_1, PRODUCT_2));

    mockMvc
        .perform(post("/cart/{userId}/checkout", USER_ID).contentType(APPLICATION_JSON))
        .andExpect(status().isAccepted());

    await()
        .atMost(20, TimeUnit.SECONDS)
        .until(
            () -> {
              final ConsumerRecords<String, OrderCreatedEvent> records =
                  kafkaConsumer.poll(Duration.ofMillis(500));
              if (records.isEmpty()) {
                return false;
              }

              assertEquals(1, records.count());
              records.forEach(
                  record -> {
                    assertEquals(USER_ID, record.value().getUserId());
                    assertEquals(EUR, record.value().getCurrency());
                    assertEquals(9.90, record.value().getAmount());
                    assertEquals(2, record.value().getProducts().size());
                  });
              return true;
            });

    kafkaConsumer.unsubscribe();
    kafkaConsumer.close();
  }

  @Test
  @Order(5)
  public void cartUserIdItemsGet_isOk_afterCheckout() throws Exception {
    mockMvc
        .perform(get("/cart/{userId}/items", USER_ID).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(0)));
  }

  @Test
  @Order(6)
  public void cartUserIdItemsDelete_isOk() throws Exception {
    mockMvc
        .perform(delete("/cart/{userId}/items", USER_ID).contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
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
