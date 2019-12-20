## vertx-spring-boot-starter-kafka

Vert.x Spring Boot Kafka starter implements a Reactor API for the Vert.x Kafka client.

### Usage

Add the starter dependency to your `pom.xml`.
```xml
<dependency>
  <groupId>dev.snowdrop</groupId>
  <artifactId>vertx-spring-boot-starter-kafka</artifactId>
</dependency>
```

#### Sending messages

Use a `ProducerRecord` builder to create a producer record.
```java
ProducerRecord<String, String> record = ProducerRecord
    .builder("my-topic", "my-record-body", "my-record-key")
    .withHeader(Header.create("my-header", "my-header-value"))
    .build();
```

Inject a `KafkaProducerFactory` to your bean and use it to create a producer.
```java
KafkaProducer<String, String> producer = producerFactory.create();
```

Then send your record with the producer.
```java
producer.send(record);
```

Producer send method returns a `Mono<RecordMetadata>`. This means that a record will not be sent until you subscribe to this
`Mono`. Once the send operations will end, `Mono` will be completed with a `RecordMetadata` containing your record information 
such as its partition, offset, checksum etc. 

#### Receiving messages

Inject a `KafkaConsumerFactory` to your bean and use it to create a consumer.
```java
KafkaConsumer<String, String> consumer = consumerFactory.create();
```

There are two steps needed to start receiving the messages. First you need to subscribe to a topic and then to a messages
`Flux`.
```java
Disposable consumerDisposer = consumer
    .subscribe("my-topic")
    .thenMany(consumer.flux())
    .subscribe(record -> System.out.printon("Received a message: " + record.value()));
```

To stop receiving messages - dispose a `Flux` subscription using the `consumerDisposer`.
If you want to fully unsubscribe - use `consumer.unsubscribe()` method. This method returns `Mono<Void>`, which means that you
need to subscribe to this `Mono` in order to execute the command. E.g.
```java
consumerDisposer.dispose();
consumer.unsubscribe().block();
```

### Configuration

To enable/disable Kafka starter, set a `vertx.kafka.enabled` property to `true/false` (`true` is a default value).

`vertx.kafka.enabled` is the only property shared between consumers and producers. Standard Kafka consumer and producer
properties are mapped via Spring properties using the respective prefixes. E.g.
```properties
vertx.kafka.producer.bootstrap.servers=localhost:9092
vertx.kafka.producer.key.serializer=org.apache.kafka.common.serialization.StringSerializer
vertx.kafka.producer.value.serializer=org.apache.kafka.common.serialization.StringSerializer
vertx.kafka.consumer.bootstrap.servers=localhost:9092
vertx.kafka.consumer.group.id=log
vertx.kafka.consumer.key.deserializer=org.apache.kafka.common.serialization.StringDeserializer
vertx.kafka.consumer.value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

These properties will be used to create consumers and producers using their factories. If you want to override any of
the properties for a particular consumer or producer, you can pass an instance of `Map<String, String>` with overriding
properties to factory's `create` method.
