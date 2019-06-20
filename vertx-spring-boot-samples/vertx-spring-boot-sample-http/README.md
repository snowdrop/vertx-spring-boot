## Vert.x HTTP example

This example demonstrates the most basic hello world application with Vert.x WebFlux.

### Usage

Start the application in one terminal window.
```bash
java -jar target/vertx-spring-boot-sample-http.jar 
```

And access it in another.
```bash
http localhost:8080/hello # Should return a default message
http localhost:8080/hello?name=John # Should return a personalized message
```
