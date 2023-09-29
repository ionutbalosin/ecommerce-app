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
package ionutbalosin.training.ecommerce.shopping.cart.listener;

import static ionutbalosin.training.ecommerce.shopping.cart.cache.ProductCache.CACHE_INSTANCE;
import static ionutbalosin.training.ecommerce.shopping.cart.listener.ProductCdcEventListener.PRODUCT_DATABASE_TOPIC;
import static java.util.UUID.fromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import ionutbalosin.training.ecommerce.message.schema.product.ProductCdcKey;
import ionutbalosin.training.ecommerce.message.schema.product.ProductCdcValue;
import ionutbalosin.training.ecommerce.shopping.cart.KafkaContainerConfiguration;
import ionutbalosin.training.ecommerce.shopping.cart.KafkaSingletonContainer;
import ionutbalosin.training.ecommerce.shopping.cart.model.ProductItem;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest(
    properties = {
      "product-service.name=localhost",
      "product-service-endpoint.url=http://localhost:8080"
    })
@Import(KafkaContainerConfiguration.class)
public class ProductCdcEventListenerTest {

  private final ProductCdcValue CDC_VALUE = getProductCdcValue();
  private final ProductCdcKey CDC_KEY = getProductCdcKey();

  @Container
  private static final KafkaContainer KAFKA_CONTAINER =
      KafkaSingletonContainer.INSTANCE.getContainer();

  @Autowired private ProductCdcEventListener classUnderTest;
  @Autowired private KafkaTemplate<ProductCdcKey, ProductCdcValue> kafkaTemplate;

  @BeforeAll
  public static void setUp() {
    KafkaSingletonContainer.INSTANCE.start();
  }

  @AfterAll
  public static void tearDown() {
    KafkaSingletonContainer.INSTANCE.stop();
  }

  @Test
  @DirtiesContext
  public void receive() {
    final Optional<ProductItem> existingProduct =
        CACHE_INSTANCE.getProduct(fromString(CDC_VALUE.getId()));
    assertEquals(false, existingProduct.isPresent());

    kafkaTemplate.send(PRODUCT_DATABASE_TOPIC, CDC_KEY, CDC_VALUE);

    await()
        .atMost(20, TimeUnit.SECONDS)
        .until(
            () -> {
              final Optional<ProductItem> cachedProductOpt =
                  CACHE_INSTANCE.getProduct(fromString(CDC_VALUE.getId()));
              if (cachedProductOpt.isEmpty()) {
                return false;
              }

              assertEquals(true, cachedProductOpt.isPresent());

              final ProductItem cachedProduct = cachedProductOpt.get();
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
    cdcValue.setId("b6b89618-4152-11ed-b878-0242ac120002");
    cdcValue.setName("Original Blend Coffee");
    cdcValue.setBrand("Dunkin'");
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
    final ByteBuffer byteBuffer = ByteBuffer.allocate(8);
    byteBuffer.putDouble(11.0);
    byteBuffer.flip();
    return byteBuffer;
  }
}
