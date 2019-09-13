## vertx-spring-boot-starter-amqp

Vert.x Spring Boot AMQP starter adapts Vert.x AMQP client to be used with Reactor API.

### Usage

Add the starter dependency to your `pom.xml`.
```xml
<dependency>
  <groupId>dev.snowdrop</groupId>
  <artifactId>vertx-spring-boot-starter-amqp</artifactId>
</dependency>
```

Inject AmqpClient wherever you need to send or receive an AMQP message.
```java
@Component
public class MyComponent {
    private final AmqpClient amqpClient;

    public MyComponent(AmqpClient amqpClient) {
        this.amqpClient = amqpClient;
    }
    // ...
}
```

Create messages using message builder.
```java
@Component
public class MyComponent {
    // ...
    public AmqpMessage createMessage(String id, String body) {
        return AmqpMessage.create()
            .id(id)
            .withBody(body)
            .build();
    }
}
```





Send messages with a sender.
```java
@Component
public class MyComponent {
    private final AmqpClient amqpClient;
    // ...
    public Mono<Void> send(String address, AmqpMessage message) {
        return amqpClient.createSender(address)
            .map(sender -> sender.send(message))
            .flatMap(AmqpSender::close);
    }
}
```

Receive messages with a receiver.
```java
@Component
public class MyComponent {
    private final AmqpClient amqpClient;
    // ...
    public Flux<AmqpMessage> receive(String address) {
        return client.createReceiver(address)
            .flatMapMany(receiver -> receiver.flux()
                .doOnCancel(() -> receiver.close().block()));
    }
}
```

### Configuration

All options from `io.vertx.amqp.AmqpClientOptions` are mapped to Spring properties under `vertx.amqp` prefix.
For example, to set an AMQP broker's host add the following property to your `application.properties` file.
```properties
vertx.amqp.host=localhost
``` 

For the full list of available properties see [AmqpProperties.java](./src/main/java/dev/snowdrop/vertx/amqp/AmqpProperties.java).

In addition, sender and receiver specific properties can be used when creating such objects. For the full set of options
see [AmqpSenderOptions.java](./src/main/java/dev/snowdrop/vertx/amqp/AmqpSenderOptions.java) and
[AmqpReceiverOptions.java](./src/main/java/dev/snowdrop/vertx/amqp/AmqpReceiverOptions.java) respectively.
