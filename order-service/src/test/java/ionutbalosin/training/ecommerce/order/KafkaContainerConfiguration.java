package ionutbalosin.training.ecommerce.order;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static io.confluent.kafka.serializers.KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG;
import static ionutbalosin.training.ecommerce.order.KafkaSingletonContainer.INSTANCE;
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
public class KafkaContainerConfiguration {

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
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, INSTANCE.getContainer().getBootstrapServers());
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
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, INSTANCE.getContainer().getBootstrapServers());
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
