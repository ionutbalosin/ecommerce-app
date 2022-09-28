# eCommerce Application

This application is used for the didactic purpose only. It is a support project for [Ionut Balosin](https://www.ionutbalosin.com/training)'s training.

Please visit the author's [website](https://www.ionutbalosin.com) for more details.

For the full copyright and license information, please view the LICENSE file that was distributed with this source code.

### High-level description

The purpose of the eCommerce platform is to have a functioning online store where customers can find products, shop around using a cart, check out the products and initiate payments.

The provided services are:
- *Product service* - handles the product catalog and exposes an API to create, retrieve, update, and delete products. It comes with a prefilled product catalog.
- *Shopping cart service* - handles the user's shopping cart and exposes an API to create, retrieve, update, delete and check out the user's shopping cart items.
- *Order service* - handles the user's orders and exposes an API to retrieve the historical orders and update them
- *Payment service* - handles the orders' payments using an external system

### Software architecture diagram

> TODO

Among the **design approaches, and styles** demonstrated in this project:

- Event-driven microservices architecture
- API-driven development
- Schema-first approach
- Shared-nothing database approach
- REST architectural style
- Scalability (i.e., horizontal scalability)
- Location decoupling
- Resiliency
- Caching
- Asynchronous logging

Among the **technologies, frameworks, and libraries** included in this project:

- [Spring Boot](https://spring.io/projects/spring-boot)
- [JdbcTemplate](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html) (e.g., query, update, batch update)
- [Caffeine](https://github.com/ben-manes/caffeine) caching
- [Kafka](https://kafka.apache.org)
- [PostgreSQL](https://www.postgresql.org)
- [Flywaydb](https://flywaydb.org)
- [Testcontainers](https://www.testcontainers.org)
- [Resilience4j](https://github.com/resilience4j/resilience4j) (e.g., circuit breaker, bulkhead)
- [Traefik](https://traefik.io) (i.e., HTTP reverse proxy and load balancer)
- [AVRO](https://avro.apache.org) and [JSON](https://www.json.org) data format
- [OpenAPI](https://www.openapis.org/) specification
- [Logback](https://logback.qos.ch/)
- [Spotless](https://github.com/diffplug/spotless) code formatter
- [Docker compose](https://docs.docker.com/compose/)

## Set-up

Please make sure you have properly installed (and configured):

- JDK 17 (i.e., latest LTS)
- Docker
- Postman

## Compile, run tests, and package

```
./mvnw spotless:apply package
```

## Bootstrap all the services (and the dependencies) with Docker

```
./bootstrap.sh
```

## Services overview via UI 

Open a browser and navigate to http://localhost:26060 to access the Traefik UI (e.g., endpoints, routes, services, etc.). 

Open a browser and navigate to http://localhost:19000 to access the Kafdrop UI (e.g., topics, partitions, consumers, messages, etc.).

**Note:** these ports are configured in Docker compose yaml files.

## Local tests with Postman

- open Postman
- import the provided API collections, corresponding to each project, as follows:
  - `./ecommerce-app/order-service/postman`
  - `./ecommerce-app/product-service/postman`
  - `./ecommerce-app/shopping-cart-service/postman`
- to simulate a basic test scenario please trigger below requests (in this sequence):

```
GET http://localhost:{{order_service_port}}/products
```
```
POST http://localhost:{{shopping-cart-service_port}}/cart/{{userId}}/items
```
```
POST http://localhost:{{shopping-cart-service_port}}/cart/{{userId}}/checkout
```
```
GET http://localhost:{{order-service_port}}/orders/{{userId}}/history
```

**Note:** all ports are already configured as variables in Postman