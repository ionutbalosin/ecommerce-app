package ionutbalosin.training.ecommerce.shopping.cart;

import static org.testcontainers.utility.DockerImageName.parse;

import org.testcontainers.containers.PostgreSQLContainer;

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
