package ionutbalosin.training.ecommerce.shopping.cart.controller;

import static ionutbalosin.training.ecommerce.shopping.cart.util.JsonUtil.asJsonString;
import static java.util.List.of;
import static java.util.UUID.fromString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import ionutbalosin.training.ecommerce.event.schema.order.OrderCreatedEvent;
import ionutbalosin.training.ecommerce.product.api.model.ProductDto;
import ionutbalosin.training.ecommerce.shopping.cart.KafkaSingletonContainer;
import ionutbalosin.training.ecommerce.shopping.cart.PostgresqlSingletonContainer;
import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemCreateDto;
import ionutbalosin.training.ecommerce.shopping.cart.api.model.CartItemUpdateDto;
import ionutbalosin.training.ecommerce.shopping.cart.service.ProductService;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest(properties = {"max.cart.items.per.request=3"})
@AutoConfigureMockMvc
@Import(CartControllerITest.KafkaContainerConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartControllerITest {

  private static final UUID USER_ID = fromString("42424242-4242-4242-4242-424242424242");
  private static final UUID FAKE_CART_ITEM_ID = fromString("00000000-0000-0000-0000-000000000000");

  @Container
  private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER =
      PostgresqlSingletonContainer.INSTANCE.getContainer();

  @Container
  private static final KafkaContainer KAFKA_CONTAINER =
      KafkaSingletonContainer.INSTANCE.getContainer();

  @Autowired private MockMvc mockMvc;
  @MockBean ProductService productService;

  final ProductDto PRODUCT_1 =
      new ProductDto()
          .productId(UUID.fromString("8134fd12-3403-11ed-a261-0242ac120002"))
          .name("Monkey Coffee")
          .brand("Zoo Land")
          .category("Beverage")
          .price(BigDecimal.valueOf(11.0))
          .currency(ProductDto.CurrencyEnum.EUR)
          .quantity(111);

  final ProductDto PRODUCT_2 =
      new ProductDto()
          .productId(UUID.fromString("77359e48-3403-11ed-a261-0242ac120002"))
          .name("Tiger Coffee")
          .brand("Wonder Land")
          .category("Beverage")
          .price(BigDecimal.valueOf(22.0))
          .currency(ProductDto.CurrencyEnum.EUR)
          .quantity(222);

  final CartItemCreateDto CART_ITEM_1 =
      new CartItemCreateDto()
          .productId(PRODUCT_1.getProductId())
          .quantity(1)
          .discount(BigDecimal.valueOf(15.0));

  final CartItemCreateDto CART_ITEM_2 =
      new CartItemCreateDto()
          .productId(PRODUCT_2.getProductId())
          .quantity(2)
          .discount(BigDecimal.valueOf(15.0));

  final CartItemUpdateDto CART_ITEM_UPDATE = new CartItemUpdateDto().quantity(3);

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
    final List<ProductDto> products = of(PRODUCT_1, PRODUCT_2);
    final KafkaConsumer<String, OrderCreatedEvent> kafkaConsumer =
        new KafkaConsumer(new KafkaContainerConfiguration().consumerConfigs());
    kafkaConsumer.subscribe(of("ecommerce-orders-topic"));

    Mockito.when(productService.getProducts(any())).thenReturn(products);

    mockMvc
        .perform(post("/cart/{userId}/checkout", USER_ID).contentType(APPLICATION_JSON))
        .andExpect(status().isCreated());

    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              final ConsumerRecords<String, OrderCreatedEvent> records =
                  kafkaConsumer.poll(Duration.ofMillis(500));
              if (records.isEmpty()) {
                return false;
              }

              assertThat(records.count(), is(1));
              records.forEach(
                  record -> {
                    assertThat(record.value().getUserId(), is(USER_ID));
                    assertThat(record.value().getProducts().size(), is(products.size()));
                  });
              return true;
            });
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

  @TestConfiguration
  public static class KafkaContainerConfiguration {

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent>
        kafkaListenerContainerFactory() {
      ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory =
          new ConcurrentKafkaListenerContainerFactory<>();
      factory.setConsumerFactory(consumerFactory());
      return factory;
    }

    @Bean
    public ConsumerFactory<String, OrderCreatedEvent> consumerFactory() {
      return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
      Map<String, Object> props = new HashMap<>();
      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
      props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
      props.put(ConsumerConfig.GROUP_ID_CONFIG, "ecommerce_group_id");
      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
      props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://testUrl");
      props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
      return props;
    }

    @Bean
    public ProducerFactory<String, OrderCreatedEvent> producerFactory() {
      Map<String, Object> props = new HashMap<>();
      props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
      props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
      props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
      props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://testUrl");
      return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate() {
      return new KafkaTemplate<>(producerFactory());
    }
  }
}
