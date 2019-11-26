package dev.snowdrop.vertx.kafka.it;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import dev.snowdrop.vertx.kafka.ConsumerRecord;
import dev.snowdrop.vertx.kafka.KafkaConsumer;
import dev.snowdrop.vertx.kafka.KafkaConsumerFactory;
import dev.snowdrop.vertx.kafka.KafkaProducer;
import dev.snowdrop.vertx.kafka.KafkaProducerFactory;
import dev.snowdrop.vertx.kafka.KafkaProperties;
import dev.snowdrop.vertx.kafka.ProducerRecord;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.serialization.BufferDeserializer;
import io.vertx.kafka.client.serialization.BufferSerializer;
import io.vertx.kafka.client.serialization.JsonArrayDeserializer;
import io.vertx.kafka.client.serialization.JsonArraySerializer;
import io.vertx.kafka.client.serialization.JsonObjectDeserializer;
import io.vertx.kafka.client.serialization.JsonObjectSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
    "vertx.kafka.producer.key.serializer=org.apache.kafka.common.serialization.StringSerializer",
    "vertx.kafka.consumer.key.deserializer=org.apache.kafka.common.serialization.StringDeserializer",
    "vertx.kafka.consumer.group.id=test"
})
@EmbeddedKafka(partitions = 1)
public class VertxSerializationIT {

    @Autowired
    private EmbeddedKafkaBroker broker;

    @Autowired
    private KafkaProperties properties;

    @Autowired
    private KafkaProducerFactory producerFactory;

    @Autowired
    private KafkaConsumerFactory consumerFactory;

    private List<KafkaProducer<?, ?>> producersToCleanup = new LinkedList<>();

    private List<KafkaConsumer<?, ?>> consumersToCleanup = new LinkedList<>();

    @Before
    public void setUp() {
        setBootstrapServers(broker.getBrokersAsString());
    }

    @After
    public void tearDown() {
        producersToCleanup.stream()
            .map(KafkaProducer::close)
            .forEach(Mono::block);
        consumersToCleanup.stream()
            .map(KafkaConsumer::close)
            .forEach(Mono::block);
    }

    @Test
    public void shouldSendAndReceiveJsonObject() throws InterruptedException {
        KafkaConsumer<String, JsonObject> consumer =
            createConsumer(JsonObject.class, JsonObjectDeserializer.class.getName());
        AtomicReference<JsonObject> jsonContainer = new AtomicReference<>();
        String topic = "json-object-topic";

        subscribe(consumer, topic, r -> jsonContainer.set(r.value()));
        waitForAssignmentPropagation();

        KafkaProducer<String, JsonObject> producer =
            createProducer(JsonObject.class, JsonObjectSerializer.class.getName());
        sendToTopic(producer, topic, JsonObject.mapFrom(singletonMap("k1", "v1")));

        await()
            .atMost(Duration.ofSeconds(5))
            .untilAtomic(jsonContainer, is(notNullValue()));

        assertThat(jsonContainer.get().getString("k1")).isEqualTo("v1");
    }

    @Test
    public void shouldSendAndReceiveJsonArray() throws InterruptedException {
        KafkaConsumer<String, JsonArray> consumer =
            createConsumer(JsonArray.class, JsonArrayDeserializer.class.getName());
        AtomicReference<JsonArray> jsonContainer = new AtomicReference<>();
        String topic = "json-array-topic";

        subscribe(consumer, topic, r -> jsonContainer.set(r.value()));
        waitForAssignmentPropagation();

        KafkaProducer<String, JsonArray> producer =
            createProducer(JsonArray.class, JsonArraySerializer.class.getName());
        sendToTopic(producer, topic, new JsonArray(singletonList("v1")));

        await()
            .atMost(Duration.ofSeconds(5))
            .untilAtomic(jsonContainer, is(notNullValue()));

        assertThat(jsonContainer.get().getString(0)).isEqualTo("v1");
    }

    @Test
    public void shouldSendAndReceiveBuffer() throws InterruptedException {
        KafkaConsumer<String, Buffer> consumer = createConsumer(Buffer.class, BufferDeserializer.class.getName());
        AtomicReference<Buffer> jsonContainer = new AtomicReference<>();
        String topic = "json-buffer-topic";

        subscribe(consumer, topic, r -> jsonContainer.set(r.value()));
        waitForAssignmentPropagation();

        KafkaProducer<String, Buffer> producer =
            createProducer(Buffer.class, BufferSerializer.class.getName());
        sendToTopic(producer, topic, Buffer.buffer("v1"));

        await()
            .atMost(Duration.ofSeconds(5))
            .untilAtomic(jsonContainer, is(notNullValue()));

        assertThat(jsonContainer.get()).isEqualTo(Buffer.buffer("v1"));
    }

    private <V> KafkaProducer<String, V> createProducer(Class<V> valueType, String valueSerializer) {
        KafkaProducer<String, V> producer =
            producerFactory.create(singletonMap("value.serializer", valueSerializer));
        // Preserve the producer for cleanup after a test
        producersToCleanup.add(producer);

        return producer;
    }

    private <V> KafkaConsumer<String, V> createConsumer(Class<V> valueType, String valueDeserializer) {
        KafkaConsumer<String, V> consumer =
            consumerFactory.create(singletonMap("value.deserializer", valueDeserializer));
        // Preserve the consumer for cleanup after a test
        consumersToCleanup.add(consumer);

        return consumer;
    }

    private <V> void subscribe(KafkaConsumer<String, V> consumer, String topic,
        Consumer<ConsumerRecord<String, V>> handler) {
        consumer
            .flux()
            .log(consumer + " receiving")
            .subscribe(handler);
        consumer
            .subscribe(topic)
            .block();
    }

    private <V> void sendToTopic(KafkaProducer<String, V> producer, String topic, V value) {
        producer
            .send(ProducerRecord.<String, V>builder(topic, value).build())
            .block();
    }

    private void waitForAssignmentPropagation() throws InterruptedException {
        // Give Kafka some time to execute partition assignment
        Thread.sleep(2000);
    }

    private void setBootstrapServers(String bootstrapServers) {
        // Workaround for Spring Kafka 2.2.11. In 2.3.x property can be injected automatically
        Map<String, String> producerConfig = properties.getProducer();
        producerConfig.put("bootstrap.servers", bootstrapServers);
        properties.setProducer(producerConfig);

        Map<String, String> consumerConfig = properties.getConsumer();
        consumerConfig.put("bootstrap.servers", bootstrapServers);
        properties.setConsumer(consumerConfig);
    }
}
