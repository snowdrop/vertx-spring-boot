package dev.snowdrop.vertx.kafka.it;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.snowdrop.vertx.kafka.KafkaProducer;
import dev.snowdrop.vertx.kafka.KafkaProducerFactory;
import dev.snowdrop.vertx.kafka.KafkaProperties;
import dev.snowdrop.vertx.kafka.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(properties = {
    "vertx.kafka.producer.key.serializer=org.apache.kafka.common.serialization.StringSerializer",
    "vertx.kafka.producer.value.serializer=org.apache.kafka.common.serialization.StringSerializer"
})
@EmbeddedKafka(topics = "test", partitions = 1)
public class ProducerIT extends AbstractIT {

    @Autowired
    private EmbeddedKafkaBroker broker;

    @Autowired
    private KafkaProperties properties;

    @Autowired
    private KafkaProducerFactory producerFactory;

    @BeforeEach
    public void setUp() {
        super.setUp(producerFactory, null, properties, broker);
    }

    @AfterEach
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void shouldSend() {
        ProducerRecord<String, String> record = ProducerRecord
            .<String, String>builder("test", "test-value")
            .build();

        KafkaProducer<String, String> producer = createProducer();
        StepVerifier.create(producer.send(record))
            .assertNext(metadata -> {
                assertThat(metadata.topic()).isEqualTo("test");
                assertThat(metadata.partition()).isEqualTo(0);
            })
            .verifyComplete();
    }

    @Test
    public void shouldWrite() {
        ProducerRecord<String, String> record = ProducerRecord
            .<String, String>builder("test", "test-value")
            .build();

        KafkaProducer<String, String> producer = createProducer();
        StepVerifier.create(producer.write(record))
            .verifyComplete();
    }

    @Test
    public void shouldGetPartitionInfo() {
        KafkaProducer<String, String> producer = createProducer();
        StepVerifier.create(producer.partitionsFor("test"))
            .assertNext(partitionInfo -> {
                assertThat(partitionInfo.getTopic()).isEqualTo("test");
                assertThat(partitionInfo.getPartition()).isEqualTo(0);
                assertThat(partitionInfo.getLeader()).isNotNull();
                assertThat(partitionInfo.getReplicas()).isNotEmpty();
                assertThat(partitionInfo.getInSyncReplicas()).isNotEmpty();
            })
            .verifyComplete();
    }

    @Test
    public void shouldCloseAndHandleException() {
        ProducerRecord<String, String> record = ProducerRecord
            .<String, String>builder("test", "test-value")
            .build();

        AtomicBoolean wasExceptionHandled = new AtomicBoolean(false);

        KafkaProducer<String, String> producer = createProducer();
        producer.exceptionHandler(t -> wasExceptionHandled.set(true));

        StepVerifier.create(producer.close())
            .verifyComplete();

        StepVerifier.create(producer.write(record))
            .verifyErrorMessage("Cannot perform operation after producer has been closed");

        await()
            .atMost(Duration.ofSeconds(2))
            .untilTrue(wasExceptionHandled);
    }
}
