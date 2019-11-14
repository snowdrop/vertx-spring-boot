package dev.snowdrop.vertx.kafka.it;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.snowdrop.vertx.kafka.KafkaProducer;
import dev.snowdrop.vertx.kafka.ProducerRecord;
import dev.snowdrop.vertx.kafka.SnowdropKafkaProducer;
import io.vertx.core.Vertx;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@EmbeddedKafka(topics = "test", partitions = 1, bootstrapServersProperty = "test.kafka.bootstrap.servers")
public class ProducerIT {

    @Autowired
    private Vertx vertx;

    @Value("${test.kafka.bootstrap.servers}")
    private String bootstrapServers;

    private KafkaProducer<String, String> producer;

    @Before
    public void setUp() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", bootstrapServers);
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new SnowdropKafkaProducer<>(
            io.vertx.axle.kafka.client.producer.KafkaProducer.create(io.vertx.axle.core.Vertx.newInstance(vertx),
                config));
    }

    @After
    public void tearDown() {
        producer.close().block();
    }

    @Test
    public void shouldSend() {
        ProducerRecord<String, String> record = ProducerRecord
            .<String, String>builder("test", "test-value")
            .build();

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

        StepVerifier.create(producer.write(record))
            .verifyComplete();
    }

    @Test
    public void shouldGetPartitionInfo() {
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
        producer.exceptionHandler(t -> wasExceptionHandled.set(true));

        StepVerifier.create(producer.close())
            .verifyComplete();

        StepVerifier.create(producer.write(record))
            .verifyErrorMessage("Cannot perform operation after producer has been closed");

        assertThat(wasExceptionHandled.get()).isTrue();
    }
}
