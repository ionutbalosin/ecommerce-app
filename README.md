# eCommerce Application

eCommerce application is used for the didactic purpose only. It is a support project for [Ionut Balosin](https://www.ionutbalosin.com/training)'s training.

Please visit the author's [website](https://www.ionutbalosin.com) for more details.

For the full copyright and license information, please view the LICENSE file that was distributed with this source code.

### High-level description

The purpose of this application is to have a platform where customers can find products, shop around using a cart, check out the products and initiate payments.

The provided services are:
- *Product service* - handles the product catalog and exposes an API to create, retrieve, update, and delete products. It comes with a prefilled product catalog.
- *Shopping cart service* - handles the user's shopping cart and exposes an API to create, retrieve, update, delete and check out the user's shopping cart items.
- *Order service* - handles the user's orders and exposes an API to retrieve the historical orders and update them
- *Payment service* - handles the orders' payments using an external system

### Software architecture diagram

<img src="diagrams/software-architecture-diagram.svg">

Among the **architectural styles, design tactics, and patterns** demonstrated in this project:

- Event-driven microservices architecture
- API-driven development approach
- Schema-first development approach
- Shared-nothing database approach
- Schema registry
- REST architectural style (e.g., RESTful APIs)
- Scalability (e.g., horizontal scalability)
- Location decoupling
- Resiliency
- Caching (e.g., local/embedded)
- Asynchronous logging
- Change data capture design pattern
- Listen to yourself design pattern
- Data transfer object enterprise application pattern

Among the **technologies, frameworks, and libraries** included in this project:

- [Spring Boot](https://spring.io/projects/spring-boot)
- [JdbcTemplate](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html) (e.g., query, update, batch update)
- [Caffeine](https://github.com/ben-manes/caffeine) caching
- [Kafka](https://kafka.apache.org)
- [PostgreSQL](https://www.postgresql.org)
- [Flywaydb](https://flywaydb.org)
- [Debezium](https://debezium.io/)
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
- cURL
- Postman

## Compile, run tests, and package

```
./mvnw spotless:apply package
```

**Note** The argument `spotless:apply` is optional but nice to have it

## Bootstrap all the services (and the dependencies) with Docker

```
./bootstrap.sh
```

To check if all Docker containers are up and running execute the below command:

```
docker ps -a
```

> **Important:** On Windows using Docker compose v2 there is a known [limitation](https://github.com/docker/compose/issues/8530) while horizontally scaling a service by using port ranges like "8080-8081:80".
> 
> At the moment, the workaround is to simply copy-paste (multiple times) the same service inside the Docker compose YAML file and explicitly assign different ports to each.

## Register the PostgreSQL connector

The PostgreSQL connector is used to monitor and record the row-level changes in the PostgreSQL database schema.

**Note:** please make sure all services (e.g., PostgreSQL and Kafka) are up and running before registering the connector 

```
./debezium-register-postgres.sh
```

## Services overview via UI 

Open a browser and navigate to http://localhost:26060 to access the Traefik UI (it shows endpoints, routes, services, etc.). 

Open a browser and navigate to http://localhost:19000 to access the Kafdrop UI (it shows topics, partitions, consumers, messages, etc.).

Open a browser and navigate to http://localhost:18080 to access the Debezium UI (it shows the PostgreSQL connector).

**Note:** these ports are configured in Docker compose YAML files.

## Local tests with Postman

- open Postman
- import the provided API collections, corresponding to each project, as follows:
  - `./ecommerce-app/order-service/postman`
  - `./ecommerce-app/product-service/postman`
  - `./ecommerce-app/shopping-cart-service/postman`
- to simulate a basic test scenario please trigger the below requests (in this sequence):

```
GET http://localhost:{{port}}/products
```
```
POST http://localhost:{{port}}/cart/{{userId}}/items
```
```
POST http://localhost:{{port}}/cart/{{userId}}/checkout
```
```
GET http://localhost:{{port}}/orders/{{userId}}/history
```

**Note:** the ports (i.e., service port or Traefik load balancer port) are already configured as variables in the Postman collections

## TODOs
A few TODO items might be:

- implement a user service (to handle user identities)
- implement a notification service (to handle all kinds of notifications)
- implement APIs pagination
- implement database bulk updates for the remaining APIs
- minimize the object creation in (DTO/entity) mappers