package dev.snowdrop.vertx.kafka.it;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import dev.snowdrop.vertx.kafka.ConsumerRecord;
import dev.snowdrop.vertx.kafka.KafkaConsumer;
import dev.snowdrop.vertx.kafka.KafkaConsumerFactory;
import dev.snowdrop.vertx.kafka.KafkaProducer;
import dev.snowdrop.vertx.kafka.KafkaProducerFactory;
import dev.snowdrop.vertx.kafka.KafkaProperties;
import dev.snowdrop.vertx.kafka.ProducerRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
    "vertx.kafka.producer.key.serializer=org.apache.kafka.common.serialization.StringSerializer",
    "vertx.kafka.producer.value.serializer=org.apache.kafka.common.serialization.StringSerializer",
    "vertx.kafka.consumer.key.deserializer=org.apache.kafka.common.serialization.StringDeserializer",
    "vertx.kafka.consumer.value.deserializer=org.apache.kafka.common.serialization.StringDeserializer"
})
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "vertx.kafka.producer.bootstrap.servers")
public class SinglePartitionE2EIT {

    @Autowired
    private KafkaProperties properties;

    @Autowired
    private KafkaProducerFactory producerFactory;

    @Autowired
    private KafkaConsumerFactory consumerFactory;

    private KafkaProducer<String, String> producer;

    private List<KafkaConsumer<String, String>> consumers = new LinkedList<>();

    private KafkaConsumer<String, String> alternativeConsumer;

    @Before
    public void setUp() {
        producer = producerFactory.create();
    }

    @After
    public void tearDown() {
        producer.close().block(Duration.ofSeconds(5));

        consumers.stream()
            .map(KafkaConsumer::close)
            .forEach(Mono::block);
    }

    @Test
    public void shouldSendAndReceiveWithSingleConsumer() throws InterruptedException {
        KafkaConsumer<String, String> consumer = createConsumer("single-consumer-main");
        List<ConsumerRecord<String, String>> records = new CopyOnWriteArrayList<>();
        String topic = "single-consumer";

        subscribeAndWaitForAssignment(consumer, topic, records::add);
        sendToTopic(producer, topic, "k1", "v1");
        sendToTopic(producer, topic, "k2", "v2");

        await()
            .atMost(Duration.ofSeconds(5))
            .untilAsserted(() -> assertThat(records).hasSize(2));

        assertConsumerRecord(records.get(0), topic, "k1", "v1", 0);
        assertConsumerRecord(records.get(1), topic, "k2", "v2", 1);
    }

    @Test
    public void shouldSendAndReceiveWithTwoConsumerGroups() throws InterruptedException {
        KafkaConsumer<String, String> firstConsumer = createConsumer("two-groups-main");
        KafkaConsumer<String, String> secondConsumer = createConsumer("two-groups-alternative");
        List<ConsumerRecord<String, String>> firstConsumerRecords = new CopyOnWriteArrayList<>();
        List<ConsumerRecord<String, String>> secondConsumerRecords = new CopyOnWriteArrayList<>();
        String topic = "two-groups";

        subscribeAndWaitForAssignment(firstConsumer, topic, firstConsumerRecords::add);
        subscribeAndWaitForAssignment(secondConsumer, topic, secondConsumerRecords::add);
        sendToTopic(producer, topic, "k1", "v1");
        sendToTopic(producer, topic, "k2", "v2");

        await()
            .atMost(Duration.ofSeconds(5))
            .untilAsserted(() -> {
                assertThat(firstConsumerRecords).hasSize(2);
                assertThat(secondConsumerRecords).hasSize(2);
            });

        assertThat(firstConsumerRecords).containsOnlyElementsOf(secondConsumerRecords);

        assertConsumerRecord(firstConsumerRecords.get(0), topic, "k1", "v1", 0);
        assertConsumerRecord(firstConsumerRecords.get(1), topic, "k2", "v2", 1);
    }

    private void assertConsumerRecord(ConsumerRecord<String, String> record, String topic, String key, String value,
        int offset) {

        assertThat(record.topic()).isEqualTo(topic);
        assertThat(record.partition()).isEqualTo(0);
        assertThat(record.key()).isEqualTo(key);
        assertThat(record.value()).isEqualTo(value);
        assertThat(record.offset()).isEqualTo(offset);
    }

    private KafkaConsumer<String, String> createConsumer(String groupId) {
        HashMap<String, String> config = new HashMap<>();
        config.putIfAbsent("group.id", groupId);
        // Embedded kafka only sets one property with bootstrap servers.
        // In this case it's producer property thus we need to set consumer property manually.
        config.putIfAbsent("bootstrap.servers", properties.getProducer().get("bootstrap.servers"));

        KafkaConsumer<String, String> consumer = consumerFactory.create(config);
        // Preserve the consumer for cleanup after a test
        consumers.add(consumer);

        return consumer;
    }

    private void subscribeAndWaitForAssignment(KafkaConsumer<String, String> consumer, String topic,
        Consumer<ConsumerRecord<String, String>> handler) throws InterruptedException {

        consumer
            .flux()
            .subscribe(handler);

        CountDownLatch latch = new CountDownLatch(1);
        consumer.partitionsAssignedHandler(partitions -> latch.countDown());

        consumer
            .subscribe(topic)
            .block();

        latch.await();
    }

    private void sendToTopic(KafkaProducer<String, String> producer, String topic, String key, String value) {
        producer
            .send(ProducerRecord.builder(topic, value, key).build())
            .block();
    }
}
