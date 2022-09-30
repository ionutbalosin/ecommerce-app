package ionutbalosin.training.ecommerce.shopping.cart.service;

import ionutbalosin.training.ecommerce.message.schema.product.ProductCdcKey;
import ionutbalosin.training.ecommerce.message.schema.product.ProductCdcValue;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
//
// This listener implements the "Change Data Capture" pattern
// It receives all updates on the PRODUCTS database table via the Debezium connector for PostgreSQL
@Service
public class ProductCdcEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductCdcEventListener.class);

  public static final String PRODUCT_DATABASE_TOPIC = "ecommerce-product-cdc-topic";

  @KafkaListener(topics = PRODUCT_DATABASE_TOPIC, groupId = "ecommerce_group_id")
  public void consume(final ConsumerRecord<ProductCdcKey, ProductCdcValue> cdcEventRecord) {
    LOGGER.debug(
        "Consumed message '{}' '{}' from Kafka topic '{}'",
        cdcEventRecord.key(),
        cdcEventRecord.value(),
        PRODUCT_DATABASE_TOPIC);
  }
}
