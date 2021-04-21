package dev.snowdrop.vertx.kafka;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import io.smallrye.mutiny.Uni;
import io.vertx.kafka.client.common.PartitionInfo;
import io.vertx.kafka.client.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SnowdropKafkaProducerTest {

    @Mock
    private io.vertx.mutiny.kafka.client.producer.KafkaProducer<Integer, String> mockMutinyProducer;

    @Mock
    private io.vertx.kafka.client.producer.KafkaProducer<Integer, String> mockVertxProducer;

    private KafkaProducer<Integer, String> producer;

    @BeforeEach
    public void setUp() {
        producer = new SnowdropKafkaProducer<>(mockMutinyProducer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSend() {
        // Setup a response to be returned by Mutiny producer
        RecordMetadata vertxRecordMetadata = new RecordMetadata(1, 2, 3, 4, "t");
        given(mockMutinyProducer.send(any()))
            .willReturn(Uni.createFrom().item(vertxRecordMetadata));

        // Create snowdrop producer record and send it
        ProducerRecord<Integer, String> record = ProducerRecord
            .builder("topic", "value", 1)
            .withTimestamp(2)
            .withPartition(3)
            .withHeader(Header.create("h1", "v1"))
            .build();
        StepVerifier.create(producer.send(record))
            .expectNext(new SnowdropRecordMetadata(vertxRecordMetadata))
            .verifyComplete();

        // Capture Mutiny producer record submitted by snowdrop producer
        ArgumentCaptor<io.vertx.mutiny.kafka.client.producer.KafkaProducerRecord<Integer, String>> mutinyRecordCaptor =
            ArgumentCaptor.forClass(io.vertx.mutiny.kafka.client.producer.KafkaProducerRecord.class);
        verify(mockMutinyProducer).send(mutinyRecordCaptor.capture());
        io.vertx.mutiny.kafka.client.producer.KafkaProducerRecord<Integer, String> mutinyRecord =
            mutinyRecordCaptor.getValue();

        // Verify that snowdrop producer converted records correctly
        assertThat(mutinyRecord.topic()).isEqualTo("topic");
        assertThat(mutinyRecord.value()).isEqualTo("value");
        assertThat(mutinyRecord.key()).isEqualTo(1);
        assertThat(mutinyRecord.timestamp()).isEqualTo(2);
        assertThat(mutinyRecord.partition()).isEqualTo(3);
        assertThat(mutinyRecord.headers()).hasSize(1);
        assertThat(mutinyRecord.headers().get(0).key()).isEqualTo("h1");
        assertThat(mutinyRecord.headers().get(0).value().toString()).isEqualTo("v1");
    }

    @Test
    public void shouldGetPartition() {
        PartitionInfo firstPartitionInfo = mock(PartitionInfo.class);
        PartitionInfo secondPartitionInfo = mock(PartitionInfo.class);

        given(mockMutinyProducer.partitionsFor("test-topic"))
            .willReturn(Uni.createFrom().item(Arrays.asList(firstPartitionInfo, secondPartitionInfo)));

        StepVerifier.create(producer.partitionsFor("test-topic"))
            .expectNext(new SnowdropPartitionInfo(firstPartitionInfo))
            .expectNext(new SnowdropPartitionInfo(secondPartitionInfo))
            .verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldFlush() {
        given(mockMutinyProducer.flush(any()))
            .will(a -> {
                ((Consumer<Void>) a.getArgument(0)).accept(null);
                return mockMutinyProducer;
            });

        StepVerifier.create(producer.flush())
            .verifyComplete();
    }

    @Test
    public void shouldHandleFlushFailure() {
        given(mockMutinyProducer.flush(any()))
            .willThrow(new RuntimeException("test"));

        StepVerifier.create(producer.flush())
            .verifyErrorMessage("test");
    }

    @Test
    public void shouldClose() {
        given(mockMutinyProducer.close())
            .willReturn(Uni.createFrom().voidItem());

        StepVerifier.create(producer.close())
            .verifyComplete();
    }

    @Test
    public void shouldCloseWithTimeout() {
        given(mockMutinyProducer.close(1L))
            .willReturn(Uni.createFrom().voidItem());

        StepVerifier.create(producer.close(1L))
            .verifyComplete();
    }

    @Test
    public void shouldDoOnVertxProducer() {
        given(mockMutinyProducer.getDelegate())
            .willReturn(mockVertxProducer);

        AtomicReference<io.vertx.kafka.client.producer.KafkaProducer<Integer, String>> vertxConsumer =
            new AtomicReference<>();
        Function<io.vertx.kafka.client.producer.KafkaProducer<Integer, String>, Boolean> function = vp -> {
            vertxConsumer.set(vp);
            return true;
        };

        StepVerifier.create(producer.doOnVertxProducer(function))
            .expectNext(true)
            .verifyComplete();
        assertThat(vertxConsumer.get()).isEqualTo(mockVertxProducer);
    }

    @Test
    public void shouldAddExceptionHandler() {
        Consumer<Throwable> handler = System.out::println;

        producer.exceptionHandler(handler);

        verify(mockMutinyProducer).exceptionHandler(handler);
    }

    @Test
    public void shouldAddDrainHandler() {
        Consumer<Void> handler = System.out::println;

        producer.drainHandler(handler);

        verify(mockMutinyProducer).drainHandler(handler);
    }

    @Test
    public void shouldWriteQueueMaxSize() {
        producer.setWriteQueueMaxSize(1);

        verify(mockMutinyProducer).setWriteQueueMaxSize(1);
    }

    @Test
    public void shouldCheckIfWriteQueueIsFull() {
        given(mockMutinyProducer.writeQueueFull()).willReturn(true);

        assertThat(producer.writeQueueFull()).isTrue();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldWrite() {
        given(mockMutinyProducer.write(any()))
            .willReturn(Uni.createFrom().voidItem());

        // Create snowdrop producer record and write it
        ProducerRecord<Integer, String> record = ProducerRecord
            .builder("topic", "value", 1)
            .withTimestamp(2)
            .withPartition(3)
            .withHeader(Header.create("h1", "v1"))
            .build();
        StepVerifier.create(producer.write(record))
            .verifyComplete();

        // Capture mutiny producer record submitted by snowdrop producer
        ArgumentCaptor<io.vertx.mutiny.kafka.client.producer.KafkaProducerRecord<Integer, String>> mutinyRecordCaptor =
            ArgumentCaptor.forClass(io.vertx.mutiny.kafka.client.producer.KafkaProducerRecord.class);
        verify(mockMutinyProducer).write(mutinyRecordCaptor.capture());
        io.vertx.mutiny.kafka.client.producer.KafkaProducerRecord<Integer, String> mutinyRecord =
            mutinyRecordCaptor.getValue();

        // Verify that snowdrop producer converted records correctly
        assertThat(mutinyRecord.topic()).isEqualTo("topic");
        assertThat(mutinyRecord.value()).isEqualTo("value");
        assertThat(mutinyRecord.key()).isEqualTo(1);
        assertThat(mutinyRecord.timestamp()).isEqualTo(2);
        assertThat(mutinyRecord.partition()).isEqualTo(3);
        assertThat(mutinyRecord.headers()).hasSize(1);
        assertThat(mutinyRecord.headers().get(0).key()).isEqualTo("h1");
        assertThat(mutinyRecord.headers().get(0).value().toString()).isEqualTo("v1");
    }

    @Test
    public void shouldEnd() {
        given(mockMutinyProducer.end())
            .willReturn(Uni.createFrom().voidItem());

        StepVerifier.create(producer.end())
            .verifyComplete();
    }

    @Test
    public void shouldGetVertxWriteStream() {
        given(mockMutinyProducer.getDelegate())
            .willReturn(mockVertxProducer);

        assertThat(producer.vertxWriteStream()).isEqualTo(mockVertxProducer);
    }
}
