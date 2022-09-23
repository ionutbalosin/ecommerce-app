package ionutbalosin.training.ecommerce.shopping.cart;

import static org.testcontainers.utility.DockerImageName.parse;

import org.testcontainers.containers.PostgreSQLContainer;

/*
 * (c) 2022 Ionut Balosin
 * Website: www.ionutbalosin.com
 * Twitter: @ionutbalosin
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 */
public enum PostgresqlSingletonContainer {
  INSTANCE();

  private final PostgreSQLContainer container;

  PostgresqlSingletonContainer() {
    container = new PostgreSQLContainer(parse("postgres:14"));
    container.start();
  }

  public PostgreSQLContainer getContainer() {
    return container;
  }
}
