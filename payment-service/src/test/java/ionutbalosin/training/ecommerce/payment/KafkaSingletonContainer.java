package ionutbalosin.training.ecommerce.payment;

import static org.testcontainers.utility.DockerImageName.parse;

import org.testcontainers.containers.KafkaContainer;

public enum KafkaSingletonContainer {
  INSTANCE();

  private final KafkaContainer container;

  KafkaSingletonContainer() {
    container = new KafkaContainer(parse("confluentinc/cp-kafka:7.2.1")).withEmbeddedZookeeper();

    container.start();
  }

  public KafkaContainer getContainer() {
    return container;
  }
}
