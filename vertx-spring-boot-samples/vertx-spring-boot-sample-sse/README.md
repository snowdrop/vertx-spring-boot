## Vert.x SSE example

This example demonstrates a simple server-sent events (SSE) service.
On a HTTP GET request service starts periodically generating random integers and returning them as an event stream values.
This event stream will continue until the request is canceled.

To use this application you will need two terminal windows. In the first one start the application:
```
> java -jar target/vertx-spring-boot-sample-sse.jar
```

Then in another initiate a request:
```
> curl localhost:8080
```

Or if you use HTTPie:
```
> http --stream :8080
```

Eventually use use `ctrl+c` to cancel the request.
