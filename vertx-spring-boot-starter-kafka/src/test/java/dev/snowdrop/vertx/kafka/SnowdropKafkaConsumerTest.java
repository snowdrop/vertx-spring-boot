package dev.snowdrop.vertx.kafka;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import io.vertx.kafka.client.common.PartitionInfo;
import io.vertx.kafka.client.common.TopicPartition;
import io.vertx.kafka.client.consumer.OffsetAndMetadata;
import io.vertx.kafka.client.consumer.OffsetAndTimestamp;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static java.util.Arrays.asList;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class SnowdropKafkaConsumerTest {

    @Mock
    private io.vertx.kafka.client.consumer.KafkaConsumer<Integer, String> mockVertxConsumer;

    @Mock
    private io.vertx.axle.kafka.client.consumer.KafkaConsumer<Integer, String> mockAxleConsumer;

    @Mock
    private io.vertx.axle.kafka.client.consumer.KafkaConsumerRecord<Integer, String> mockAxleConsumerRecord;

    private SnowdropKafkaConsumer<Integer, String> consumer;

    @Before
    public void setUp() {
        consumer = new SnowdropKafkaConsumer<>(mockAxleConsumer);
    }

    @Test
    public void shouldSubscribeToSingleTopic() {
        given(mockAxleConsumer.subscribe("test-topic"))
            .willReturn(completedFuture(null));

        StepVerifier.create(consumer.subscribe("test-topic"))
            .verifyComplete();
    }

    @Test
    public void shouldSubscribeToMultipleTopics() {
        given(mockAxleConsumer.subscribe(toSet("test-topic-1", "test-topic-2")))
            .willReturn(completedFuture(null));

        StepVerifier.create(consumer.subscribe(Flux.just("test-topic-1", "test-topic-2")))
            .verifyComplete();
    }

    @Test
    public void shouldAssignSinglePartition() {
        given(mockAxleConsumer.assign(new TopicPartition("test-topic", 1)))
            .willReturn(completedFuture(null));

        StepVerifier.create(consumer.assign(Partition.create("test-topic", 1)))
            .verifyComplete();
    }

    @Test
    public void shouldAssignMultiplePartitions() {
        Set<TopicPartition> vertxPartitions = toSet(
            new TopicPartition("test-topic-1", 0),
            new TopicPartition("test-topic-2", 1)
        );
        given(mockAxleConsumer.assign(vertxPartitions))
            .willReturn(completedFuture(null));

        Flux<Partition> partitions = Flux.just(
            Partition.create("test-topic-1", 0),
            Partition.create("test-topic-2", 1)
        );
        StepVerifier.create(consumer.assign(partitions))
            .verifyComplete();
    }

    @Test
    public void shouldUnsubscribe() {
        given(mockAxleConsumer.unsubscribe())
            .willReturn(completedFuture(null));

        StepVerifier.create(consumer.unsubscribe())
            .verifyComplete();
    }

    @Test
    public void shouldGetSubscriptions() {
        given(mockAxleConsumer.subscription())
            .willReturn(completedFuture(toSet("test-topic-1", "test-topic-2")));

        StepVerifier.create(consumer.subscriptions())
            .expectNext("test-topic-1")
            .expectNext("test-topic-2")
            .verifyComplete();
    }

    @Test
    public void shouldGetAssignments() {
        Set<TopicPartition> vertxPartitions = toSet(
            new TopicPartition("test-topic-1", 0),
            new TopicPartition("test-topic-2", 1)
        );
        given(mockAxleConsumer.assignment())
            .willReturn(completedFuture(vertxPartitions));

        StepVerifier.create(consumer.assignments())
            .expectNext(Partition.create("test-topic-1", 0))
            .expectNext(Partition.create("test-topic-2", 1))
            .verifyComplete();
    }

    @Test
    public void shouldGetPartitionsFor() {
        PartitionInfo firstPartition = mock(PartitionInfo.class);
        PartitionInfo secondPartition = mock(PartitionInfo.class);

        given(mockAxleConsumer.partitionsFor("test-topic"))
            .willReturn(completedFuture(asList(firstPartition, secondPartition)));

        StepVerifier.create(consumer.partitionsFor("test-topic"))
            .expectNext(new SnowdropKafkaPartitionInfo(firstPartition))
            .expectNext(new SnowdropKafkaPartitionInfo(secondPartition))
            .verifyComplete();
    }

    @Test
    public void shouldAddPartitionsRevokedHandler() {
        given(mockAxleConsumer.partitionsRevokedHandler(any()))
            .will(a -> {
                Consumer<Set<TopicPartition>> handler = a.getArgument(0);
                handler.accept(toSet(
                    new TopicPartition("test-topic", 1),
                    new TopicPartition("test-topic", 2)
                ));
                return mockAxleConsumer;
            });

        AtomicReference<Flux<Partition>> futureTopicPartition = new AtomicReference<>();
        consumer.partitionsRevokedHandler(futureTopicPartition::set);

        await()
            .atMost(Duration.ofSeconds(2))
            .untilAtomic(futureTopicPartition, Matchers.is(Matchers.notNullValue()));

        Set<Partition> partitions = toSet(
            Partition.create("test-topic", 1),
            Partition.create("test-topic", 2)
        );
        StepVerifier.create(futureTopicPartition.get())
            .assertNext(p -> assertThat(partitions.remove(p)).isTrue())
            .assertNext(p -> assertThat(partitions.remove(p)).isTrue())
            .verifyComplete();
    }

    @Test
    public void shouldAddPartitionsAssignedHandler() {
        given(mockAxleConsumer.partitionsAssignedHandler(any()))
            .will(a -> {
                Consumer<Set<TopicPartition>> handler = a.getArgument(0);
                handler.accept(toSet(
                    new TopicPartition("test-topic", 1),
                    new TopicPartition("test-topic", 2)
                ));
                return mockAxleConsumer;
            });

        AtomicReference<Flux<Partition>> futureTopicPartition = new AtomicReference<>();
        consumer.partitionsAssignedHandler(futureTopicPartition::set);

        await()
            .atMost(Duration.ofSeconds(2))
            .untilAtomic(futureTopicPartition, Matchers.is(Matchers.notNullValue()));

        Set<Partition> partitions = toSet(
            Partition.create("test-topic", 1),
            Partition.create("test-topic", 2)
        );
        StepVerifier.create(futureTopicPartition.get())
            .assertNext(p -> assertThat(partitions.remove(p)).isTrue())
            .assertNext(p -> assertThat(partitions.remove(p)).isTrue())
            .verifyComplete();
    }

    @Test
    public void shouldSeek() {
        given(mockAxleConsumer.seek(new TopicPartition("test-topic", 1), 2))
            .willReturn(completedFuture(null));

        StepVerifier.create(consumer.seek(Partition.create("test-topic", 1), 2))
            .verifyComplete();
    }

    @Test
    public void shouldSeekToBeginningOfSinglePartition() {
        given(mockAxleConsumer.seekToBeginning(new TopicPartition("test-topic", 1)))
            .willReturn(completedFuture(null));

        StepVerifier.create(consumer.seekToBeginning(Partition.create("test-topic", 1)))
            .verifyComplete();
    }

    @Test
    public void shouldSeekToBeginningOfMultiplePartitions() {
        given(mockAxleConsumer.seekToBeginning(toSet(
            new TopicPartition("test-topic", 1),
            new TopicPartition("test-topic", 2)))
        ).willReturn(completedFuture(null));

        StepVerifier
            .create(consumer.seekToBeginning(Flux.just(
                Partition.create("test-topic", 1),
                Partition.create("test-topic", 2)))
            ).verifyComplete();
    }

    @Test
    public void shouldSeekToEndOfSinglePartition() {
        given(mockAxleConsumer.seekToEnd(new TopicPartition("test-topic", 1)))
            .willReturn(completedFuture(null));

        StepVerifier.create(consumer.seekToEnd(Partition.create("test-topic", 1)))
            .verifyComplete();
    }

    @Test
    public void shouldSeekToEndOfMultiplePartitions() {
        given(mockAxleConsumer.seekToEnd(toSet(
            new TopicPartition("test-topic", 1),
            new TopicPartition("test-topic", 2)))
        ).willReturn(completedFuture(null));

        StepVerifier
            .create(consumer.seekToEnd(Flux.just(
                Partition.create("test-topic", 1),
                Partition.create("test-topic", 2)))
            ).verifyComplete();
    }

    @Test
    public void shouldGetPosition() {
        given(mockAxleConsumer.position(new TopicPartition("test-topic", 1)))
            .willReturn(completedFuture(1L));

        StepVerifier.create(consumer.position(Partition.create("test-topic", 1)))
            .expectNext(1L)
            .verifyComplete();
    }

    @Test
    public void shouldGetCommitted() {
        given(mockAxleConsumer.committed(new TopicPartition("test-topic", 1)))
            .willReturn(completedFuture(new OffsetAndMetadata(2, "test-metadata")));

        StepVerifier.create(consumer.committed(Partition.create("test-topic", 1)))
            .expectNext(2L)
            .verifyComplete();
    }

    @Test
    public void shouldGetBeginningOffset() {
        given(mockAxleConsumer.beginningOffsets(new TopicPartition("test-topic", 1)))
            .willReturn(completedFuture(2L));

        StepVerifier.create(consumer.beginningOffset(Partition.create("test-topic", 1)))
            .expectNext(2L)
            .verifyComplete();
    }

    @Test
    public void shouldGetEndOffset() {
        given(mockAxleConsumer.endOffsets(new TopicPartition("test-topic", 1)))
            .willReturn(completedFuture(2L));

        StepVerifier.create(consumer.endOffset(Partition.create("test-topic", 1)))
            .expectNext(2L)
            .verifyComplete();
    }

    @Test
    public void shouldGetTimeOffset() {
        given(mockAxleConsumer.offsetsForTimes(new TopicPartition("test-topic", 1), 2L))
            .willReturn(completedFuture(new OffsetAndTimestamp(2L, 3L)));

        StepVerifier.create(consumer.timeOffset(Partition.create("test-topic", 1), 2L))
            .expectNext(2L)
            .verifyComplete();
    }

    @Test
    public void shouldCommit() {
        given(mockAxleConsumer.commit())
            .willReturn(completedFuture(null));

        StepVerifier.create(consumer.commit())
            .verifyComplete();
    }

    @Test
    public void shouldClose() {
        given(mockAxleConsumer.close())
            .willReturn(completedFuture(null));

        StepVerifier.create(consumer.close())
            .verifyComplete();
    }

    @Test
    public void shouldDoOnVertxConsumer() {
        given(mockAxleConsumer.getDelegate())
            .willReturn(mockVertxConsumer);

        AtomicReference<io.vertx.kafka.client.consumer.KafkaConsumer<Integer, String>> vertxConsumer =
            new AtomicReference<>();
        Function<io.vertx.kafka.client.consumer.KafkaConsumer<Integer, String>, Boolean> function = vc -> {
            vertxConsumer.set(vc);
            return true;
        };

        StepVerifier.create(consumer.doOnVertxConsumer(function))
            .expectNext(true)
            .verifyComplete();
        assertThat(vertxConsumer.get()).isEqualTo(mockVertxConsumer);
    }

    @Test
    public void shouldGetMono() {
        given(mockAxleConsumer.toPublisher()).willReturn(Flux.just(mockAxleConsumerRecord));

        StepVerifier.create(consumer.mono())
            .expectNext(new SnowdropConsumerRecord<>(mockAxleConsumerRecord))
            .verifyComplete();
    }

    @Test
    public void shouldGetFlux() {
        given(mockAxleConsumer.toPublisher()).willReturn(Flux.just(mockAxleConsumerRecord));

        StepVerifier.create(consumer.flux())
            .expectNext(new SnowdropConsumerRecord<>(mockAxleConsumerRecord))
            .verifyComplete();
    }

    @Test
    public void shouldGetVertxReadStream() {
        given(mockAxleConsumer.getDelegate()).willReturn(mockVertxConsumer);

        assertThat(consumer.vertxReadStream()).isEqualTo(mockVertxConsumer);
    }

    @SafeVarargs
    private final <T> Set<T> toSet(T... elements) {
        return new HashSet<T>(asList(elements));
    }
}
