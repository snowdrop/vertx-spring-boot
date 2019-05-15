## Vert.x Spring Boot

Following features are available at the moment.

* Vertx core instance injection
* Vert.x HTTP server and client integration to WebFlux framework
* Vert.x WebSocket server and client integration to WebFlux framework
* Vert.x mail client Reactor API adaptation

## Building artifacts

```
> ./mvnw clean install
```

## Samples

* [Chunked](./vertx-spring-boot-samples/vertx-spring-boot-sample-chunked) - demonstrates chunked data handling. Receives data from https://httpbin.org, forwards it to the front end as well as sends batches of it via email.
* [HTTP](./vertx-spring-boot-samples/vertx-spring-boot-sample-http) - simple hello world service.
* [HTTP OAuth2](./vertx-spring-boot-samples/vertx-spring-boot-sample-http-oauth) - demonstrates authentication with GitHub.
* [HTTP Security](./vertx-spring-boot-samples/vertx-spring-boot-sample-http-security) - demonstrates basic and form authentication.
* [Mail](./vertx-spring-boot-samples/vertx-spring-boot-sample-mail) - demonstrates mail client usage.
