package ionutbalosin.training.ecommerce.shopping.cart;

import static org.testcontainers.utility.DockerImageName.parse;

import org.testcontainers.containers.KafkaContainer;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
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
