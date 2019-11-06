package dev.snowdrop.vertx.kafka;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import io.vertx.kafka.client.common.Node;
import io.vertx.kafka.client.common.PartitionInfo;
import io.vertx.kafka.client.producer.RecordMetadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SnowdropKafkaProducerTest {

    @Mock
    private io.vertx.axle.kafka.client.producer.KafkaProducer<Integer, String> mockAxleKafkaProducer;

    @Mock
    private io.vertx.kafka.client.producer.KafkaProducer<Integer, String> mockVertxKafkaProducer;

    @Mock
    private org.apache.kafka.clients.producer.Producer<Integer, String> mockKafkaProducer;

    private KafkaProducer<Integer, String> producer;

    @Before
    public void setUp() {
        producer = new SnowdropKafkaProducer<>(mockAxleKafkaProducer);
    }

    @Test
    public void shouldSend() {
        KafkaProducerRecord<Integer, String> record = KafkaProducerRecord
            .builder("topic", "value", Integer.class)
            .build();
        RecordMetadata vertxRecordMetadata = new RecordMetadata(1, 2, 3, 4, "topic");

        given(mockAxleKafkaProducer.send(any()))
            .willReturn(CompletableFuture.completedFuture(vertxRecordMetadata));

        StepVerifier.create(producer.send(record))
            .expectNext(new SnowdropKafkaRecordMetadata(vertxRecordMetadata))
            .verifyComplete();
    }

    @Test
    public void shouldGetPartition() {
        List<Node> vertxKafkaNodes = Arrays.asList(
            new Node(true, "test-host", 1, "1", true, 8080, "test-rack"),
            new Node(true, "test-host", 2, "2", true, 8080, "test-rack")
        );
        PartitionInfo vertxKafkaPartitionInfo =
            new PartitionInfo(vertxKafkaNodes, vertxKafkaNodes.get(0), 1, vertxKafkaNodes, "test-topic");

        given(mockAxleKafkaProducer.partitionsFor("test-topic")).willReturn(
            CompletableFuture.completedFuture(Collections.singletonList(vertxKafkaPartitionInfo)));

        StepVerifier.create(producer.partitionFor("test-topic"))
            .expectNext(new SnowdropKafkaPartitionInfo(vertxKafkaPartitionInfo))
            .verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldFlush() {
        given(mockAxleKafkaProducer.flush(any())).will(a -> {
            ((Consumer<Void>) a.getArgument(0)).accept(null);
            return mockAxleKafkaProducer;
        });

        StepVerifier.create(producer.flush())
            .verifyComplete();
    }

    @Test
    public void shouldHandleFlushFailure() {
        given(mockAxleKafkaProducer.flush(any())).willThrow(new RuntimeException("test"));

        StepVerifier.create(producer.flush())
            .verifyErrorMessage("test");
    }

    @Test
    public void shouldClose() {
        given(mockAxleKafkaProducer.close()).willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(producer.close())
            .verifyComplete();
    }

    @Test
    public void shouldCloseWithTimeout() {
        given(mockAxleKafkaProducer.close(1L)).willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(producer.close(1L))
            .verifyComplete();
    }

    @Test
    public void shouldUnwrap() {
        given(mockAxleKafkaProducer.getDelegate()).willReturn(mockVertxKafkaProducer);
        given(mockVertxKafkaProducer.unwrap()).willReturn(mockKafkaProducer);

        assertThat(producer.unwrap()).isEqualTo(mockKafkaProducer);
    }

    @Test
    public void shouldAddExceptionHandler() {
        Consumer<Throwable> handler = System.out::println;

        producer.exceptionHandler(handler);

        verify(mockAxleKafkaProducer).exceptionHandler(handler);
    }

    @Test
    public void shouldAddDrainHandler() {
        Consumer<Void> handler = System.out::println;

        producer.drainHandler(handler);

        verify(mockAxleKafkaProducer).drainHandler(handler);
    }

    @Test
    public void shouldWriteQueueMaxSize() {
        producer.setWriteQueueMaxSize(1);

        verify(mockAxleKafkaProducer).setWriteQueueMaxSize(1);
    }

    @Test
    public void shouldCheckIfWriteQueueIsFull() {
        given(mockAxleKafkaProducer.writeQueueFull()).willReturn(true);

        assertThat(producer.writeQueueFull()).isTrue();
    }

    @Test
    public void shouldWrite() {
        KafkaProducerRecord<Integer, String> data = KafkaProducerRecord
            .builder("topic", "value", Integer.class)
            .build();

        given(mockAxleKafkaProducer.write(any())).willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(producer.write(data))
            .verifyComplete();
    }

    @Test
    public void shouldEnd() {
        given(mockAxleKafkaProducer.end()).willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(producer.end())
            .verifyComplete();
    }

    @Test
    public void shouldGetVertxWriteStream() {
        given(mockAxleKafkaProducer.getDelegate()).willReturn(mockVertxKafkaProducer);

        assertThat(producer.vertxWriteStream()).isEqualTo(mockVertxKafkaProducer);
    }
}
