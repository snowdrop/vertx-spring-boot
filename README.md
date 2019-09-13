## Vert.x Spring Boot

Following starters are available at the moment.

* [Vert.x core starter](./vertx-spring-boot-starter)
* [Vert.x HTTP starter](./vertx-spring-boot-starter-http)
* Vert.x HTTP Test starter
* [Vert.x actuator starter](./vertx-spring-boot-starter-actuator)
* [Vert.x mail starter](./vertx-spring-boot-starter-mail)
* [Vert.x AMQP starter](./vertx-spring-boot-starter-amqp)

## Samples

* [Chunked](./vertx-spring-boot-samples/vertx-spring-boot-sample-chunked) - demonstrates chunked data handling. Receives data from https://httpbin.org, forwards it to the front end as well as sends batches of it via email.
* [HTTP](./vertx-spring-boot-samples/vertx-spring-boot-sample-http) - simple hello world service.
* [HTTP OAuth2](./vertx-spring-boot-samples/vertx-spring-boot-sample-http-oauth) - demonstrates authentication with GitHub.
* [HTTP Security](./vertx-spring-boot-samples/vertx-spring-boot-sample-http-security) - demonstrates basic and form authentication.
* [Mail](./vertx-spring-boot-samples/vertx-spring-boot-sample-mail) - demonstrates mail client usage.
* [AMQP](./vertx-spring-boot-samples/vertx-spring-boot-sample-amqp) - demonstrates AMQP client usage.

## Building the project from source

```bash
./mvnw clean install
```

