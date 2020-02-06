## Vert.x WebSocket example

This example demonstrates a simple WebSocket interaction between a browser and a backend service.
Once an application's web page is open, it initiates a WebSocket with a backend service.
The web page contains a from which user can use to send values to the backend service via WebSocket.
Backend service converts the received value to uppercase and send it back via the same WebSocket.
Returned values will appear in a list below the form.

## Usage

Build the application.
```bash
$ mvn package
```

Start the application.
```bash
$ java -jar target/vertx-spring-boot-sample-websocket.jar
```

Open the application in your web browser at [http://localhost:8080/index.html](http://localhost:8080/index.html)

## Implementation

Backend service is implemented in [WebSocketSampleApplication.java](src/main/java/dev/snowdrop/vertx/sample/websocket/WebSocketSampleApplication.java).
This class starts the application and registers a WebSocket handler.

Web page is implemented in [index.html](src/main/resources/static/index.html).
It defines a simple HTML form and the javascript functions to handle WebSocket communication.

[WebSocketSampleApplicationTest.java](src/test/java/dev/snowdrop/vertx/sample/websocket/WebSocketSampleApplicationTest.java) tests the application with a WebSocket client.

