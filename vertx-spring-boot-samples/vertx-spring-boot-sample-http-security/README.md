## Vert.x HTTP security example

This example demonstrates how to use Spring security for basic and form authentication in Vert.x WebFlux application.

### Usage

Start the application in a terminal.
```bash
java -jar target/vertx-spring-boot-sample-http-security-0.0.1-SNAPSHOT.jar
```

Open http://localhost:8080 in your browser. You should be redirected to a login page. Use user:user credentials to authenticate.
To logout go to http://localhost:8080/logout.

Try basic authentication via terminal.
```bash
http localhost:8080 # should result in 401 Unauthorized error
http -a user:user localhost:8080 # should return a hello message
```
