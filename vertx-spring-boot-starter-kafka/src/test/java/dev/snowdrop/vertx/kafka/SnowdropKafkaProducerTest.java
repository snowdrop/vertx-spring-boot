package dev.snowdrop.vertx.kafka;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import io.vertx.kafka.client.common.PartitionInfo;
import io.vertx.kafka.client.producer.RecordMetadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.test.StepVerifier;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SnowdropKafkaProducerTest {

    @Mock
    private io.vertx.axle.kafka.client.producer.KafkaProducer<Integer, String> mockAxleProducer;

    @Mock
    private io.vertx.kafka.client.producer.KafkaProducer<Integer, String> mockVertxProducer;

    private KafkaProducer<Integer, String> producer;

    @Before
    public void setUp() {
        producer = new SnowdropKafkaProducer<>(mockAxleProducer);
    }

    @Test
    public void shouldSend() {
        RecordMetadata vertxRecordMetadata = mock(RecordMetadata.class);

        given(mockAxleProducer.send(any()))
            .willReturn(completedFuture(vertxRecordMetadata));

        KafkaProducerRecord<Integer, String> record = KafkaProducerRecord
            .builder("topic", "value", Integer.class)
            .build();

        StepVerifier.create(producer.send(record))
            .expectNext(new SnowdropKafkaRecordMetadata(vertxRecordMetadata))
            .verifyComplete();
    }

    @Test
    public void shouldGetPartition() {
        PartitionInfo firstPartitionInfo = mock(PartitionInfo.class);
        PartitionInfo secondPartitionInfo = mock(PartitionInfo.class);

        given(mockAxleProducer.partitionsFor("test-topic"))
            .willReturn(completedFuture(Arrays.asList(firstPartitionInfo, secondPartitionInfo)));

        StepVerifier.create(producer.partitionsFor("test-topic"))
            .expectNext(new SnowdropKafkaPartitionInfo(firstPartitionInfo))
            .expectNext(new SnowdropKafkaPartitionInfo(secondPartitionInfo))
            .verifyComplete();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldFlush() {
        given(mockAxleProducer.flush(any()))
            .will(a -> {
                ((Consumer<Void>) a.getArgument(0)).accept(null);
                return mockAxleProducer;
            });

        StepVerifier.create(producer.flush())
            .verifyComplete();
    }

    @Test
    public void shouldHandleFlushFailure() {
        given(mockAxleProducer.flush(any()))
            .willThrow(new RuntimeException("test"));

        StepVerifier.create(producer.flush())
            .verifyErrorMessage("test");
    }

    @Test
    public void shouldClose() {
        given(mockAxleProducer.close())
            .willReturn(completedFuture(null));

        StepVerifier.create(producer.close())
            .verifyComplete();
    }

    @Test
    public void shouldCloseWithTimeout() {
        given(mockAxleProducer.close(1L))
            .willReturn(completedFuture(null));

        StepVerifier.create(producer.close(1L))
            .verifyComplete();
    }

    @Test
    public void shouldDoOnVertxProducer() {
        given(mockAxleProducer.getDelegate())
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

        verify(mockAxleProducer).exceptionHandler(handler);
    }

    @Test
    public void shouldAddDrainHandler() {
        Consumer<Void> handler = System.out::println;

        producer.drainHandler(handler);

        verify(mockAxleProducer).drainHandler(handler);
    }

    @Test
    public void shouldWriteQueueMaxSize() {
        producer.setWriteQueueMaxSize(1);

        verify(mockAxleProducer).setWriteQueueMaxSize(1);
    }

    @Test
    public void shouldCheckIfWriteQueueIsFull() {
        given(mockAxleProducer.writeQueueFull()).willReturn(true);

        assertThat(producer.writeQueueFull()).isTrue();
    }

    @Test
    public void shouldWrite() {
        KafkaProducerRecord<Integer, String> data = KafkaProducerRecord
            .builder("topic", "value", Integer.class)
            .build();

        given(mockAxleProducer.write(any()))
            .willReturn(completedFuture(null));

        StepVerifier.create(producer.write(data))
            .verifyComplete();
    }

    @Test
    public void shouldEnd() {
        given(mockAxleProducer.end())
            .willReturn(completedFuture(null));

        StepVerifier.create(producer.end())
            .verifyComplete();
    }

    @Test
    public void shouldGetVertxWriteStream() {
        given(mockAxleProducer.getDelegate())
            .willReturn(mockVertxProducer);

        assertThat(producer.vertxWriteStream()).isEqualTo(mockVertxProducer);
    }
}
