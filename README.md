# eCommerce Application

The repository is a support project for [Ionut Balosin](https://www.ionutbalosin.com/training)'s training.

Please visit the author's [website](https://www.ionutbalosin.com) for more details.

For the full copyright and license information, please view the LICENSE file that was distributed with this source code.

Among the **design approaches, and styles** demonstrated in this project:

- Event-driven microservices architecture
- API-driven development
- Schema-first approach
- Shared-nothing database approach
- REST architectural style
- Location decoupling
- Resiliency
- Caching
- Asynchronous logging

Among the **technologies, frameworks, and libraries** included in this project:

- Spring Boot
- JdbcTemplate
- Caffeine
- Kafka
- PostgreSQL
- Flywaydb
- Testcontainers
- Resilience4j
- AVRO and JSON data format
- OpenAPI
- Spotless

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
