package dev.snowdrop.vertx.kafka;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.kafka.client.common.PartitionInfo;
import io.vertx.kafka.client.common.TopicPartition;
import io.vertx.kafka.client.consumer.OffsetAndMetadata;
import io.vertx.kafka.client.consumer.OffsetAndTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class SnowdropKafkaConsumerTest {

    @Mock
    private io.vertx.kafka.client.consumer.KafkaConsumer<Integer, String> mockVertxConsumer;

    @Mock
    private io.vertx.mutiny.kafka.client.consumer.KafkaConsumer<Integer, String> mockMutinyConsumer;

    @Mock
    private io.vertx.mutiny.kafka.client.consumer.KafkaConsumerRecord<Integer, String> mockMutinyConsumerRecord;

    private SnowdropKafkaConsumer<Integer, String> consumer;

    @BeforeEach
    public void setUp() {
        consumer = new SnowdropKafkaConsumer<>(mockMutinyConsumer);
    }

    @Test
    public void shouldSubscribeToSingleTopic() {
        given(mockMutinyConsumer.subscribe("test-topic"))
            .willReturn(Uni.createFrom().voidItem());

        StepVerifier.create(consumer.subscribe("test-topic"))
            .verifyComplete();
    }

    @Test
    public void shouldSubscribeToMultipleTopics() {
        given(mockMutinyConsumer.subscribe(toSet("test-topic-1", "test-topic-2")))
            .willReturn(Uni.createFrom().voidItem());

        StepVerifier.create(consumer.subscribe(asList("test-topic-1", "test-topic-2")))
            .verifyComplete();
    }

    @Test
    public void shouldAssignSinglePartition() {
        given(mockMutinyConsumer.assign(new TopicPartition("test-topic", 1)))
            .willReturn(Uni.createFrom().voidItem());

        StepVerifier.create(consumer.assign(Partition.create("test-topic", 1)))
            .verifyComplete();
    }

    @Test
    public void shouldAssignMultiplePartitions() {
        Set<TopicPartition> vertxPartitions = toSet(
            new TopicPartition("test-topic-1", 0),
            new TopicPartition("test-topic-2", 1)
        );
        given(mockMutinyConsumer.assign(vertxPartitions))
            .willReturn(Uni.createFrom().voidItem());

        Collection<Partition> partitions = asList(
            Partition.create("test-topic-1", 0),
            Partition.create("test-topic-2", 1)
        );
        StepVerifier.create(consumer.assign(partitions))
            .verifyComplete();
    }

    @Test
    public void shouldUnsubscribe() {
        given(mockMutinyConsumer.unsubscribe())
            .willReturn(Uni.createFrom().voidItem());

        StepVerifier.create(consumer.unsubscribe())
            .verifyComplete();
    }

    @Test
    public void shouldGetSubscriptions() {
        given(mockMutinyConsumer.subscription())
            .willReturn(Uni.createFrom().item(toSet("test-topic-1", "test-topic-2")));

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
        given(mockMutinyConsumer.assignment())
            .willReturn(Uni.createFrom().item(vertxPartitions));

        StepVerifier.create(consumer.assignments())
            .expectNext(Partition.create("test-topic-1", 0))
            .expectNext(Partition.create("test-topic-2", 1))
            .verifyComplete();
    }

    @Test
    public void shouldGetPartitionsFor() {
        PartitionInfo firstPartition = mock(PartitionInfo.class);
        PartitionInfo secondPartition = mock(PartitionInfo.class);

        given(mockMutinyConsumer.partitionsFor("test-topic"))
            .willReturn(Uni.createFrom().item(asList(firstPartition, secondPartition)));

        StepVerifier.create(consumer.partitionsFor("test-topic"))
            .expectNext(new SnowdropPartitionInfo(firstPartition))
            .expectNext(new SnowdropPartitionInfo(secondPartition))
            .verifyComplete();
    }

    @Test
    public void shouldAddPartitionsRevokedHandler() {
        given(mockMutinyConsumer.partitionsRevokedHandler(any()))
            .will(a -> {
                Consumer<Set<TopicPartition>> handler = a.getArgument(0);
                handler.accept(toSet(
                    new TopicPartition("test-topic", 1),
                    new TopicPartition("test-topic", 2)
                ));
                return mockMutinyConsumer;
            });

        AtomicReference<Set<Partition>> partitions = new AtomicReference<>();
        consumer.partitionsRevokedHandler(partitions::set);

        await()
            .atMost(Duration.ofSeconds(2))
            .untilAtomic(partitions, is(notNullValue()));

        assertThat(partitions.get()).containsOnly(
            Partition.create("test-topic", 1),
            Partition.create("test-topic", 2)
        );
    }

    @Test
    public void shouldAddPartitionsAssignedHandler() {
        given(mockMutinyConsumer.partitionsAssignedHandler(any()))
            .will(a -> {
                Consumer<Set<TopicPartition>> handler = a.getArgument(0);
                handler.accept(toSet(
                    new TopicPartition("test-topic", 1),
                    new TopicPartition("test-topic", 2)
                ));
                return mockMutinyConsumer;
            });

        AtomicReference<Set<Partition>> partitions = new AtomicReference<>();
        consumer.partitionsAssignedHandler(partitions::set);

        await()
            .atMost(Duration.ofSeconds(2))
            .untilAtomic(partitions, is(notNullValue()));

        assertThat(partitions.get()).containsOnly(
            Partition.create("test-topic", 1),
            Partition.create("test-topic", 2)
        );
    }

    @Test
    public void shouldSeek() {
        given(mockMutinyConsumer.seek(new TopicPartition("test-topic", 1), 2))
            .willReturn(Uni.createFrom().voidItem());

        StepVerifier.create(consumer.seek(Partition.create("test-topic", 1), 2))
            .verifyComplete();
    }

    @Test
    public void shouldSeekToBeginningOfSinglePartition() {
        given(mockMutinyConsumer.seekToBeginning(new TopicPartition("test-topic", 1)))
            .willReturn(Uni.createFrom().voidItem());

        StepVerifier.create(consumer.seekToBeginning(Partition.create("test-topic", 1)))
            .verifyComplete();
    }

    @Test
    public void shouldSeekToBeginningOfMultiplePartitions() {
        given(mockMutinyConsumer.seekToBeginning(toSet(
            new TopicPartition("test-topic", 1),
            new TopicPartition("test-topic", 2)))
        ).willReturn(Uni.createFrom().voidItem());

        StepVerifier
            .create(consumer.seekToBeginning(asList(
                Partition.create("test-topic", 1),
                Partition.create("test-topic", 2)))
            ).verifyComplete();
    }

    @Test
    public void shouldSeekToEndOfSinglePartition() {
        given(mockMutinyConsumer.seekToEnd(new TopicPartition("test-topic", 1)))
            .willReturn(Uni.createFrom().voidItem());

        StepVerifier.create(consumer.seekToEnd(Partition.create("test-topic", 1)))
            .verifyComplete();
    }

    @Test
    public void shouldSeekToEndOfMultiplePartitions() {
        given(mockMutinyConsumer.seekToEnd(toSet(
            new TopicPartition("test-topic", 1),
            new TopicPartition("test-topic", 2)))
        ).willReturn(Uni.createFrom().voidItem());

        StepVerifier
            .create(consumer.seekToEnd(asList(
                Partition.create("test-topic", 1),
                Partition.create("test-topic", 2)))
            ).verifyComplete();
    }

    @Test
    public void shouldGetPosition() {
        given(mockMutinyConsumer.position(new TopicPartition("test-topic", 1)))
            .willReturn(Uni.createFrom().item(1L));

        StepVerifier.create(consumer.position(Partition.create("test-topic", 1)))
            .expectNext(1L)
            .verifyComplete();
    }

    @Test
    public void shouldGetCommitted() {
        given(mockMutinyConsumer.committed(new TopicPartition("test-topic", 1)))
            .willReturn(Uni.createFrom().item(new OffsetAndMetadata(2, "test-metadata")));

        StepVerifier.create(consumer.committed(Partition.create("test-topic", 1)))
            .expectNext(2L)
            .verifyComplete();
    }

    @Test
    public void shouldGetBeginningOffset() {
        given(mockMutinyConsumer.beginningOffsets(new TopicPartition("test-topic", 1)))
            .willReturn(Uni.createFrom().item(2L));

        StepVerifier.create(consumer.beginningOffset(Partition.create("test-topic", 1)))
            .expectNext(2L)
            .verifyComplete();
    }

    @Test
    public void shouldGetEndOffset() {
        given(mockMutinyConsumer.endOffsets(new TopicPartition("test-topic", 1)))
            .willReturn(Uni.createFrom().item(2L));

        StepVerifier.create(consumer.endOffset(Partition.create("test-topic", 1)))
            .expectNext(2L)
            .verifyComplete();
    }

    @Test
    public void shouldGetTimeOffset() {
        given(mockMutinyConsumer.offsetsForTimes(new TopicPartition("test-topic", 1), 2L))
            .willReturn(Uni.createFrom().item(new OffsetAndTimestamp(2L, 3L)));

        StepVerifier.create(consumer.timeOffset(Partition.create("test-topic", 1), 2L))
            .expectNext(2L)
            .verifyComplete();
    }

    @Test
    public void shouldCommit() {
        given(mockMutinyConsumer.commit())
            .willReturn(Uni.createFrom().voidItem());

        StepVerifier.create(consumer.commit())
            .verifyComplete();
    }

    @Test
    public void shouldClose() {
        given(mockMutinyConsumer.close())
            .willReturn(Uni.createFrom().voidItem());

        StepVerifier.create(consumer.close())
            .verifyComplete();
    }

    @Test
    public void shouldDoOnVertxConsumer() {
        given(mockMutinyConsumer.getDelegate())
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
        given(mockMutinyConsumer.toMulti()).willReturn(Multi.createFrom().item(mockMutinyConsumerRecord));

        StepVerifier.create(consumer.mono())
            .expectNext(new SnowdropConsumerRecord<>(mockMutinyConsumerRecord))
            .verifyComplete();
    }

    @Test
    public void shouldGetFlux() {
        given(mockMutinyConsumer.toMulti()).willReturn(Multi.createFrom().item(mockMutinyConsumerRecord));

        StepVerifier.create(consumer.flux())
            .expectNext(new SnowdropConsumerRecord<>(mockMutinyConsumerRecord))
            .verifyComplete();
    }

    @Test
    public void shouldGetVertxReadStream() {
        given(mockMutinyConsumer.getDelegate()).willReturn(mockVertxConsumer);

        assertThat(consumer.vertxReadStream()).isEqualTo(mockVertxConsumer);
    }

    @SafeVarargs
    private final <T> Set<T> toSet(T... elements) {
        return new HashSet<>(asList(elements));
    }
}
