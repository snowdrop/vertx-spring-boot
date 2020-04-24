# vertx-spring-boot-starter-kafka

Vert.x Spring Boot Kafka starter implements a Reactor API for the Vert.x Kafka client.

# Background

Vert.x Spring Boot Kafka starter integrates the Vert.x Kafka client into a Spring Boot ecosystem by providing a Reactor API for
it and implementing the configuration and beans to make it usable in a Spring Boot way.
`KafkaProducerFactory` and `KafkaConsumerFactory` are the two Spring beans that can be injected into a Spring Boot application
in order to send and receive Kafka messages.

Before going any further, though, it is important that you understand the main concepts of the Kafka broker. Check out the official
Kafka [documentation](https://kafka.apache.org/documentation) which is a great resource to start with. It is very informative
while at the same time easy to understand.

If you would like to learn more about the original Vert.x Kafka Client, check out its
[documentation](https://vertx.io/docs/vertx-kafka-client/java), which has a vast number of examples and use cases.

# Usage

Add the starter dependency to your `pom.xml`.
```xml
<dependency>
  <groupId>dev.snowdrop</groupId>
  <artifactId>vertx-spring-boot-starter-kafka</artifactId>
</dependency>
```

> Note on a reactive API. Many of the methods demonstrated below return either Mono or Flux. Therefore, in order to
> execute the functionality provided by a method you'll need to subscribe to the returned publisher (Mono or Flux).

## Producer

### Creating a producer

To create a producer, inject a `KafkaProducerFactory` to your bean and call its `create` method.
```java
KafkaProducer<String, String> producer = producerFactory.create();
```

Pass a `Map<String, String>` in order to override any producer properties.
```java
Map<String, String> config = new HashMap<>();
config.put("key.deserializer", StringDeserializer.class.getName());
config.put("value.deserializer", StringDeserializer.class.getName());

KafkaProducer<String, String> producer = producerFactory.create(config);
```

### Creating producer record

Use a `ProducerRecord` builder to create a producer record.
```java
ProducerRecord<String, String> record = ProducerRecord
        .builder("my-topic", "my-record-body", "my-record-key")
        .withHeader(Header.create("my-header", "my-header-value"))
        .build();
```

If a record doesn't have a key, you'll have to specify generic types for the builder.
```java
ProducerRecord<String, String> record = ProducerRecord.
        <String, String>builder("my-topic", "my-record-body")
        .build();
```

### Sending messages

To send a message simply invoke a `send` method with a producer record.
```java
producer.send(record);
```

Send method returns a `Mono<RecordMetadata>`. On completion, `RecordMetadata` will contain the record information such
as its partition, offset, checksum etc.

## Consumer

### Creating a consumer

To create a consumer, inject a `KafkaConsumerFactory` to your bean and call its `create` method.
```java
KafkaConsumer<String, String> consumer = consumerFactory.create();
```

Pass a `Map<String, String>` in order to override any consumer properties.
```java
Map<String, String> config = new HashMap<>();
config.put("group.id", "my-group");
config.put("key.deserializer", StringDeserializer.class.getName());
config.put("value.deserializer", StringDeserializer.class.getName());

KafkaConsumer<String, String> consumer = consumerFactory.create(config);
```

### Subscribing to a topic

To subscribe to a topic(s) as part of a consumer group (configured in consumer properties) use a `subscribe` method.
```java
consumer.subsribe("my-topic");
```
or
```java
consumer.subscribe(Arrays.asList("my-topic", "another-topic"));
```

This will tell a Kafka cluster to assign a partition to this consumer. No messages will be consumed yet, however.
Read below about how to start consuming the messages.

### Assigning a specific topic

If you want to assign a specific partition to your consumer instead of relying on a Kafka cluster to do that for you as
part of a consumer group rebalancing, you can use an `assign` method.
```java
consumer.assign(Partition.create("my-topic", 0));
```

### Seeking in a partition

A Kafka broker provides an easy way to move back and forth through the partition message history. For that you can use
`seekToBeginning` (to reset an offset to 0) or `seek` (to set an offset to a specific value) methods.
```java
consumer.seekToBeginning(Partition.create("my-topic", 0));
```
or
```java
consumer.seek(Partition.create("my-topic", 0), 10);
```


For example, if you would like to receive all the messages whenever a consumer subscribes, you could do something like this.

```java
// Register a handler to be invoked once a number of partitions are assigned
consumer.partitionsAssignedHandler(partitions -> {
    Flux.fromIterable(partitions)
            .flatMap(consumer::seekToBeginning)
            // Time out in case this takes too long
            .take(Duration.ofSeconds(2))
            .subscribe();
});
consumer.subscribe("my-topic").block();
```

### Receiving messages

Even though a consumer has subscribed to a topic and got a partition(s) assigned it will not start consuming messages
until told to do so explicitly. To do that, subscribe to the consumer's message stream.
```java
Disposable consumerDisposer = consumer.flux()
        .subscribe(this::doSomething);
```

Later once you want to stop consuming the messages you can use the disposer.
```java
consumerDisposer.dispose();
```

### Unsubscribing from a topic

To leave a consumer group, consumer can unsubscribe from a topic.
```java
consumer.unsubscribe();
```

# Configuration

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
