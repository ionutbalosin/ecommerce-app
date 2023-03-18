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

import ionutbalosin.training.ecommerce.message.schema.product.ProductCdcKey;
import ionutbalosin.training.ecommerce.message.schema.product.ProductCdcValue;
import ionutbalosin.training.ecommerce.shopping.cart.model.ProductItem;
import ionutbalosin.training.ecommerce.shopping.cart.model.mapper.ProductItemMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/*
 * This listener implements the "Change Data Capture" pattern
 * It receives all updates on the PRODUCTS database table via the Debezium connector for PostgreSQL
 */
@Service
public class ProductCdcEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductCdcEventListener.class);

  public static final String PRODUCT_DATABASE_TOPIC = "ecommerce-product-cdc-topic";

  private final ProductItemMapper mapper;

  public ProductCdcEventListener(ProductItemMapper mapper) {
    this.mapper = mapper;
  }

  @KafkaListener(topics = PRODUCT_DATABASE_TOPIC, groupId = "ecommerce_group_id")
  public void receive(final ConsumerRecord<ProductCdcKey, ProductCdcValue> cdcEventRecord) {
    LOGGER.debug(
        "Received message '{}' '{}' from Kafka topic '{}'",
        cdcEventRecord.key(),
        cdcEventRecord.value(),
        PRODUCT_DATABASE_TOPIC);
    final ProductItem productItem = mapper.map(cdcEventRecord.value());
    CACHE_INSTANCE.addProduct(productItem);
  }
}
