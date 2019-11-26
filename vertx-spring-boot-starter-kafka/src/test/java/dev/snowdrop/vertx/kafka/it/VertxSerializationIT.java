package dev.snowdrop.vertx.kafka.it;

import java.time.Duration;
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
public class VertxSerializationIT extends AbstractIT {

    private static final String VALUE_SERIALIZER = "value.serializer";

    private static final String VALUE_DESERIALIZER = "value.deserializer";

    private static final String JSON_OBJECT_SERIALIZER = JsonObjectSerializer.class.getName();

    private static final String JSON_OBJECT_DESERIALIZER = JsonObjectDeserializer.class.getName();

    private static final String JSON_ARRAY_SERIALIZER = JsonArraySerializer.class.getName();

    private static final String JSON_ARRAY_DESERIALIZER = JsonArrayDeserializer.class.getName();

    private static final String BUFFER_SERIALIZER = BufferSerializer.class.getName();

    private static final String BUFFER_DESERIALIZER = BufferDeserializer.class.getName();

    @Autowired
    private EmbeddedKafkaBroker broker;

    @Autowired
    private KafkaProperties properties;

    @Autowired
    private KafkaProducerFactory producerFactory;

    @Autowired
    private KafkaConsumerFactory consumerFactory;

    @Before
    public void setUp() {
        super.setUp(producerFactory, consumerFactory, properties, broker);
    }

    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void shouldSendAndReceiveJsonObject() throws InterruptedException {
        String topic = "json-object-topic";
        KafkaConsumer<String, JsonObject> consumer =
            createConsumer(singletonMap(VALUE_DESERIALIZER, JSON_OBJECT_DESERIALIZER));
        AtomicReference<JsonObject> result = new AtomicReference<>();

        subscribe(consumer, topic, r -> result.set(r.value()));
        waitForAssignmentPropagation();

        KafkaProducer<String, JsonObject> producer =
            createProducer(singletonMap(VALUE_SERIALIZER, JSON_OBJECT_SERIALIZER));
        sendToTopic(producer, topic, JsonObject.mapFrom(singletonMap("k1", "v1")));

        await()
            .atMost(Duration.ofSeconds(5))
            .untilAtomic(result, is(notNullValue()));

        assertThat(result.get())
            .isEqualTo(JsonObject.mapFrom(singletonMap("k1", "v1")));
    }

    @Test
    public void shouldSendAndReceiveJsonArray() throws InterruptedException {
        String topic = "json-array-topic";
        KafkaConsumer<String, JsonArray> consumer =
            createConsumer(singletonMap(VALUE_DESERIALIZER, JSON_ARRAY_DESERIALIZER));
        AtomicReference<JsonArray> result = new AtomicReference<>();

        subscribe(consumer, topic, r -> result.set(r.value()));
        waitForAssignmentPropagation();

        KafkaProducer<String, JsonArray> producer =
            createProducer(singletonMap(VALUE_SERIALIZER, JSON_ARRAY_SERIALIZER));
        sendToTopic(producer, topic, new JsonArray(singletonList("v1")));

        await()
            .atMost(Duration.ofSeconds(5))
            .untilAtomic(result, is(notNullValue()));

        assertThat(result.get())
            .isEqualTo(new JsonArray(singletonList("v1")));
    }

    @Test
    public void shouldSendAndReceiveBuffer() throws InterruptedException {
        String topic = "json-buffer-topic";
        KafkaConsumer<String, Buffer> consumer = createConsumer(singletonMap(VALUE_DESERIALIZER, BUFFER_DESERIALIZER));
        AtomicReference<Buffer> result = new AtomicReference<>();

        subscribe(consumer, topic, r -> result.set(r.value()));
        waitForAssignmentPropagation();

        KafkaProducer<String, Buffer> producer = createProducer(singletonMap(VALUE_SERIALIZER, BUFFER_SERIALIZER));
        sendToTopic(producer, topic, Buffer.buffer("v1"));

        await()
            .atMost(Duration.ofSeconds(5))
            .untilAtomic(result, is(notNullValue()));

        assertThat(result.get())
            .isEqualTo(Buffer.buffer("v1"));
    }
}
