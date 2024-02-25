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
package ionutbalosin.training.ecommerce.shipping.adapter;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static io.confluent.kafka.serializers.KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@TestConfiguration
public class KafkaContainerConfig {

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, ?> kafkaListenerContainerFactory(
      KafkaTemplate<String, ?> kafkaTemplate) {
    final ConcurrentKafkaListenerContainerFactory<String, ?> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(kafkaConsumerFactory());
    factory.setReplyTemplate(kafkaTemplate);
    return factory;
  }

  @Bean
  public ConsumerFactory<String, Object> kafkaConsumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
  }

  @Bean
  public static Map<String, Object> consumerConfigs() {
    final Map<String, Object> props = new HashMap<>();
    props.put(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
        KafkaSingletonContainer.INSTANCE.getContainer().getBootstrapServers());
    props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(GROUP_ID_CONFIG, "ecommerce_group_id");
    props.put(KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
    props.put(VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
    props.put(SCHEMA_REGISTRY_URL_CONFIG, "mock://testUrl");
    props.put(SPECIFIC_AVRO_READER_CONFIG, "true");
    return props;
  }

  @Bean
  public ProducerFactory<String, ?> kafkaProducerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfigs());
  }

  @Bean
  public static Map<String, Object> producerConfigs() {
    final Map<String, Object> props = new HashMap<>();
    props.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        KafkaSingletonContainer.INSTANCE.getContainer().getBootstrapServers());
    props.put(KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
    props.put(VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
    props.put(SCHEMA_REGISTRY_URL_CONFIG, "mock://testUrl");
    return props;
  }

  @Bean
  public KafkaTemplate<String, ?> kafkaTemplate() {
    return new KafkaTemplate<>(kafkaProducerFactory());
  }
}
