## Vert.x Kafka example

This example demonstrates a simple application with a Kafka publisher and a subscriber.

## Application workflow 

Log message
```
POST request > controller > logger > Kafka broker > log
``` 

Receiving logged messages
```
GET request > controller > log > controller > GET response
```   

### Usage

Start a Kafka cluster and configure its bootstrap URLs in
[src/main/resources/application.yml](src/main/resources/application.yml). It is done separately for producer and
consumer. In this example application it is set to `localhost:9092` by default. 

Build the application.
```bash
mvn clean package
```

Start the application in one terminal window.
```bash
java -jar target/vertx-spring-boot-sample-kafka.jar 
```

And access it in another.

First submit a couple of messages for processing.
```bash
echo "Hello, World" | http POST :8080
echo "Hello again" | http POST :8080 
```

Then check the result.
```bash
http :8080
```
You should get something like the following.
```bash
HTTP/1.1 200 OK
Content-Type: text/event-stream;charset=UTF-8
transfer-encoding: chunked

data:Hello, World

data:Hello again
```

### Kafka configuration

Kafka client configuration is very straightforward and is done via `application.(yml|properties)` file. Here's the
one used in this application.

```yaml
vertx:
  kafka:
    producer:
      bootstrap:
        # Default bootstrap servers to be used by producers        
        servers: localhost:9092
      key:
        # Default key serializer to be used by producers
        serializer: org.apache.kafka.common.serialization.StringSerializer
      value:
        # Default value serializer to be used by producers
        serializer: org.apache.kafka.common.serialization.StringSerializer
    consumer:
      bootstrap:
        # Default bootstrap servers to be used by consumers
        servers: localhost:9092
      group:
        # Default consumer group id
        id: log
      key:
        # Default key deserializer to be used by consumers
        deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value:
        # Default value deserializer to be used by consumers
        deserializer: org.apache.kafka.common.serialization.StringDeserializer

``` 

You can set any standard Kafka property by using `vertx.kafka.producer` or `vertx.kafka.consumer` prefixes for producer
and consumer respectively.
