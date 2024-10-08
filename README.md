<p align="center">
  <img alt="eCommerce" title="eCommerce" src="assets/images/ecommerce_logo_320.png">
</p>

<h1 align="center">eCommerce Application</h1>
<h4 align="center">⚡️ Architectural Empowerment: Applying Theory to Real-World Practice ⚡️</h4>

---


This repository provides practical examples and code snippets to help Java developers implement effective architectural concepts and design patterns with a main focus on performance, scalability, and resiliency as primary quality attributes.

These examples are designed to complement the curriculum of the 📚 [Designing High-Performance, Scalable, and Resilient Applications](https://ionutbalosin.com/training/designing-high-performance-scalable-resilient-applications) Course.

If you're looking to enhance your skills and design secure, high-performing applications, 🎓 [enroll](https://ionutbalosin.com/training/designing-high-performance-scalable-resilient-applications) now and master the principles of modern Java architecture!

For more resources and insights, feel free to visit my [website](https://ionutbalosin.com).

---

## Content

- [Architectural Concepts](#architectural-concepts)
- [Project Modules](#project-modules)
- [Architectural Diagrams](#architectural-diagrams)
  - [Software Architecture Diagram](#software-architecture-diagram)
  - [Sequence Diagram](#sequence-diagram)
  - [State Diagram](#state-diagram)
- [Technology Stack](#technology-stack)
- [SetUp](#setup)
- [License](#license)

## Architectural Concepts

Among the **architectural styles, design tactics, and patterns** demonstrated in this project:

- Layered architecture, Hexagonal architecture, Event-driven microservices architecture
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

## Project Modules

The purpose of this application is to have a platform where customers can find products, shop around using a cart, check out the products and initiate payments.

Below is a breakdown and description of each module in the current project.

Module                                                  | Description
------------------------------------------------------- |-------------------------------------------------------
`Account service`                                       | Manages user data and exposes a GraphQL interface for retrieving accounts. It includes a prefilled user account for demonstration purposes.
`Product service`                                       | Manages the product catalog and exposes an API to create, retrieve, update, and delete products. It includes a prefilled product catalog for quick setup.
`Shopping cart service`                                 | Manages the user's shopping cart, exposing an API to create, retrieve, update, delete, and check out items in the cart.
`Order service`                                         | Manages user orders and provides an API to retrieve historical orders and update them as needed.
`Payment service`                                       | Processes payments for orders using an external payment system.
`Shipping service`                                      | Simulates the shipment of successful orders to their destination after payment processing is completed.
`Notification service`                                  | Logs all received events to the console for monitoring and debugging purposes.
`Message schema library`                                | Contains AVRO message definitions used across services to ensure consistent data formats.

## Architectural Diagrams

### Software Architecture Diagram

<img src="assets/diagrams/software-architecture-diagram.svg">

### Sequence Diagram

End to end sequence diagram:

```mermaid
sequenceDiagram
actor User
User->>Product Service: Get Products
User->>Shopping Cart Service: Save Products In Cart
User->>Shopping Cart Service: Checkout Cart Items
Shopping Cart Service->>Order Service: Publish Order Created Event
Order Service->>Payment Service: Trigger Payment Command
critical Payment
    Payment Service->>Payment Gateway: Trigger Payment
    Payment Gateway-->>Payment Service: Payment Status (e.g., APPROVED/FAILED)
end
Payment Service-->>Order Service: Publish Payment Status Updated Event
Order Service->>Notifications Service: Publish Payment Status Updated Event
alt Payment APPROVED
Order Service->>Shipping Service: Trigger Shipping Command
Shipping Service-->>Order Service: Publish Shipping Status Updated Event (e.g., IN_PROGRESS)
critical Shipping
    Shipping Service->>Shipping Gateway: Trigger Shipping
    Shipping Gateway->>Shipping Service: Shipping Status (e.g., COMPLETED/FAILED)
end
Shipping Service-->>Order Service: Publish Shipping Status Updated Event (e.g., COMPLETED/FAILED)
Order Service->>Notifications Service: Publish Shipping Status Updated Event
end
```

### State Diagram

Events state diagram:

```mermaid
stateDiagram-v2
[*] --> NEW
NEW --> PAYMENT_APPROVED
NEW --> PAYMENT_FAILED
PAYMENT_FAILED --> [*]
PAYMENT_APPROVED --> SHIPPING_IN_PROGRESS
SHIPPING_IN_PROGRESS --> SHIPPING_COMPLETED
SHIPPING_IN_PROGRESS --> SHIPPING_FAILED
SHIPPING_FAILED --> [*]
SHIPPING_COMPLETED --> [*]
```

## Technology Stack

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
- [GraphQL](https://graphql.org/)
- [Logback](https://logback.qos.ch/)
- [Spotless](https://github.com/diffplug/spotless) code formatter
- [Docker compose](https://docs.docker.com/compose/)
- [ArchUnit](https://www.archunit.org/)

## SetUp

Please make sure you have properly installed (and configured):

- JDK 21 (i.e., latest LTS)
- Docker
- cURL
- Postman

### Compile, run tests, and package

```
./mvnw clean package
```

**Note:** Please start the Docker agent up front, otherwise, the tests fail.

### Bootstrap all the services (and the dependencies) with Docker

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

### Register the PostgreSQL connector

The PostgreSQL connector is used to monitor and record the row-level changes in the PostgreSQL database schema.

**Note:** please make sure all services (e.g., PostgreSQL and Kafka) are up and running before registering the connector 

```
./debezium-register-postgres.sh
```

Once the PostgreSQL connector is successfully registered, it reads the database log and produces events for row-level INSERT, UPDATE, and DELETE operations, emitting such events to the Kafka topic (in this case to the `ecommerce-product-cdc-topic`).

### Services overview via UI 

Open a browser and navigate to http://localhost:26060 to access the **Traefik UI** (it shows endpoints, routes, services, etc.). 

Open a browser and navigate to http://localhost:19000 to access the **Kafdrop UI** (it shows topics, partitions, consumers, messages, etc.).

Open a browser and navigate to http://localhost:18080 to access the **Debezium UI** (it shows the PostgreSQL connector).

Open a browser and navigate to http://localhost:55080/graphiql?path=/graphql to access the **GraphiQL** in-browser tool for writing, validating, and testing GraphQL `account-service` queries.

### Local tests with Postman

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

### TODOs

A few, optional, TODOs for further enhancements might be:

- implement a scheduler to reply unprocessed/failed events
- implement API pagination
- implement database bulk updates for the remaining APIs

## License


This project is licensed under the MIT License.

Please see the [LICENSE](LICENSE.md) file for full license.

```
/**
*  eCommerce Application
*
*  Copyright (c) 2022 - 2024 Ionut Balosin
*  Website: www.ionutbalosin.com
*  X: @ionutbalosin | LinkedIn: ionutbalosin | Mastodon: ionutbalosin@mastodon.social
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
```