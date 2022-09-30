package ionutbalosin.training.ecommerce.shopping.cart.service;

import static ionutbalosin.training.ecommerce.shopping.cart.service.ProductCdcEventListener.PRODUCT_DATABASE_TOPIC;
import static java.util.Set.of;
import static java.util.UUID.fromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import ionutbalosin.training.ecommerce.message.schema.product.ProductCdcKey;
import ionutbalosin.training.ecommerce.message.schema.product.ProductCdcValue;
import ionutbalosin.training.ecommerce.shopping.cart.KafkaContainerConfiguration;
import ionutbalosin.training.ecommerce.shopping.cart.KafkaSingletonContainer;
import ionutbalosin.training.ecommerce.shopping.cart.cache.ProductCache;
import ionutbalosin.training.ecommerce.shopping.cart.model.ProductItem;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
@ExtendWith(SpringExtension.class)
@Import(KafkaContainerConfiguration.class)
@SpringBootTest(
    properties = {
      "product-service.name=localhost",
      "product-service-endpoint.url=http://localhost:8080"
    })
public class ProductCdcEventListenerTest {

  private final ProductCdcValue CDC_VALUE = getProductCdcValue();
  private final ProductCdcKey CDC_KEY = getProductCdcKey();

  @Container
  private static final KafkaContainer KAFKA_CONTAINER =
      KafkaSingletonContainer.INSTANCE.getContainer();

  @Autowired private ProductCdcEventListener classUnderTest;
  @Autowired private KafkaTemplate<ProductCdcKey, ProductCdcValue> kafkaTemplate;
  @Autowired private ProductCache productCache;

  @Test
  public void consumeTest() {
    final List<ProductItem> existingProducts =
        productCache.getProducts(of(fromString(CDC_VALUE.getId())));
    assertEquals(0, existingProducts.size());

    kafkaTemplate.send(PRODUCT_DATABASE_TOPIC, CDC_KEY, CDC_VALUE);

    await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              final List<ProductItem> addedProducts =
                  productCache.getProducts(of(fromString(CDC_VALUE.getId())));
              if (addedProducts.isEmpty()) {
                return false;
              }

              assertEquals(1, addedProducts.size());

              final ProductItem cachedProduct = addedProducts.get(0);
              assertEquals(CDC_VALUE.getName(), cachedProduct.getName());
              assertEquals(CDC_VALUE.getBrand(), cachedProduct.getBrand());
              assertEquals(CDC_VALUE.getCategory(), cachedProduct.getCategory());
              assertEquals(CDC_VALUE.getCurrency(), cachedProduct.getCurrency().toString());
              assertEquals(CDC_VALUE.getQuantity(), cachedProduct.getQuantity());

              return true;
            });
  }

  private ProductCdcValue getProductCdcValue() {

    final ProductCdcValue cdcValue = new ProductCdcValue();
    cdcValue.setProductId(1);
    cdcValue.setId("02f85436-397f-11ed-a261-0242ac120002");
    cdcValue.setName("Präsident Ganze Bohne");
    cdcValue.setBrand("Julius Meinl");
    cdcValue.setCategory("Beverage");
    cdcValue.setPrice(getPrice());
    cdcValue.setCurrency("EUR");
    cdcValue.setQuantity(111);
    cdcValue.setDatIns(12L);
    cdcValue.setUsrIns("anonymous");
    cdcValue.setStat("A");
    return cdcValue;
  }

  private ProductCdcKey getProductCdcKey() {
    final ProductCdcKey cdcKey = new ProductCdcKey();
    cdcKey.setProductId(1);
    return cdcKey;
  }

  private ByteBuffer getPrice() {
    final ByteBuffer byteBuffer = ByteBuffer.allocate(4);
    byteBuffer.putFloat(11);
    byteBuffer.rewind();
    return byteBuffer;
  }
}
