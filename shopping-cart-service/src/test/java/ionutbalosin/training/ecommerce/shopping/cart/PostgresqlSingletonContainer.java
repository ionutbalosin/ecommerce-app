package ionutbalosin.training.ecommerce.shopping.cart;

import org.testcontainers.containers.PostgreSQLContainer;

public enum PostgresqlSingletonContainer {
  INSTANCE();

  private final PostgreSQLContainer container;

  PostgresqlSingletonContainer() {
    container = new PostgreSQLContainer("postgres:14");
    container.start();
  }

  public PostgreSQLContainer getContainer() {
    return container;
  }
}
