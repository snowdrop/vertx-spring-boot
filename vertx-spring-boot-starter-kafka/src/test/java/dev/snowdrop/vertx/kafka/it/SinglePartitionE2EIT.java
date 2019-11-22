package dev.snowdrop.vertx.kafka.it;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
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
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {
    "vertx.kafka.producer.key.serializer=org.apache.kafka.common.serialization.StringSerializer",
    "vertx.kafka.producer.value.serializer=org.apache.kafka.common.serialization.StringSerializer",
    "vertx.kafka.consumer.key.deserializer=org.apache.kafka.common.serialization.StringDeserializer",
    "vertx.kafka.consumer.value.deserializer=org.apache.kafka.common.serialization.StringDeserializer"
})
@EmbeddedKafka(partitions = 1)
public class SinglePartitionE2EIT {

    @Autowired
    private EmbeddedKafkaBroker broker;

    @Autowired
    private KafkaProperties properties;

    @Autowired
    private KafkaProducerFactory producerFactory;

    @Autowired
    private KafkaConsumerFactory consumerFactory;

    private KafkaProducer<String, String> producer;

    private List<KafkaConsumer<String, String>> consumers = new LinkedList<>();

    @Before
    public void setUp() {
        // Workaround for Spring Kafka 2.2.11. In 2.3.x property can be injected automatically
        Map<String, String> producerConfig = properties.getProducer();
        producerConfig.put("bootstrap.servers", broker.getBrokersAsString());
        properties.setProducer(producerConfig);

        Map<String, String> consumerConfig = properties.getConsumer();
        consumerConfig.put("bootstrap.servers", broker.getBrokersAsString());
        properties.setConsumer(consumerConfig);

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

        subscribe(consumer, topic, records::add);
        waitForAssignmentPropagation();

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

        subscribe(firstConsumer, topic, firstConsumerRecords::add);
        subscribe(secondConsumer, topic, secondConsumerRecords::add);

        waitForAssignmentPropagation();

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

    private KafkaConsumer<String, String> createConsumer(String groupId) {
        KafkaConsumer<String, String> consumer = consumerFactory.create(singletonMap("group.id", groupId));
        // Preserve the consumer for cleanup after a test
        consumers.add(consumer);

        return consumer;
    }

    private void assertConsumerRecord(ConsumerRecord<String, String> record, String topic, String key,
        String value, int offset) {

        assertThat(record.topic()).isEqualTo(topic);
        assertThat(record.partition()).isEqualTo(0);
        assertThat(record.key()).isEqualTo(key);
        assertThat(record.value()).isEqualTo(value);
        assertThat(record.offset()).isEqualTo(offset);
    }

    private void subscribe(KafkaConsumer<String, String> consumer, String topic,
        Consumer<ConsumerRecord<String, String>> handler) {

        consumer
            .flux()
            .log(consumer + " receiving")
            .subscribe(handler);
        consumer
            .subscribe(topic)
            .block();
    }

    private void sendToTopic(KafkaProducer<String, String> producer, String topic, String key, String value) {
        producer
            .send(ProducerRecord.builder(topic, value, key).build())
            .block();
    }

    private void waitForAssignmentPropagation() throws InterruptedException {
        // Give Kafka some time to execute partition assignment
        Thread.sleep(2000);
    }
}
