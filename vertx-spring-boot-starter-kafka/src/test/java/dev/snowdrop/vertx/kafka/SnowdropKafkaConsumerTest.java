package dev.snowdrop.vertx.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import io.vertx.kafka.client.common.Node;
import io.vertx.kafka.client.common.PartitionInfo;
import io.vertx.kafka.client.common.TopicPartition;
import io.vertx.kafka.client.consumer.OffsetAndMetadata;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SnowdropKafkaConsumerTest {

    @Mock
    private io.vertx.kafka.client.consumer.KafkaConsumer<Integer, String> mockVertxKafkaConsumer;

    @Mock
    private io.vertx.axle.kafka.client.consumer.KafkaConsumer<Integer, String> mockAxleKafkaConsumer;

    @Mock
    private io.vertx.axle.kafka.client.consumer.KafkaConsumerRecords<Integer, String> mockAxleKafkaConsumerRecords;

    @Mock
    private io.vertx.axle.kafka.client.consumer.KafkaConsumerRecord<Integer, String> mockAxleKafkaConsumerRecord;

    private SnowdropKafkaConsumer<Integer, String> consumer;

    @Before
    public void setUp() {
        consumer = new SnowdropKafkaConsumer<>(mockAxleKafkaConsumer);
    }

    @Test
    public void shouldSubscribeToSingleTopic() {
        given(mockAxleKafkaConsumer.subscribe("test-topic")).willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(consumer.subscribe("test-topic"))
            .verifyComplete();
    }

    @Test
    public void shouldSubscribeToMultipleTopics() {
        Set<String> topics = new HashSet<>(Arrays.asList("test-topic-1", "test-topic-2"));

        given(mockAxleKafkaConsumer.subscribe(topics)).willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(consumer.subscribe(topics))
            .verifyComplete();
    }

    @Test
    public void shouldAssignSinglePartition() {
        given(mockAxleKafkaConsumer.assign(new TopicPartition("test-topic", 1)))
            .willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(consumer.assign(KafkaTopicPartition.create("test-topic", 1)))
            .verifyComplete();
    }

    @Test
    public void shouldAssignMultiplePartitions() {
        Set<KafkaTopicPartition> kafkaTopicPartitions = new HashSet<>(Arrays.asList(
            KafkaTopicPartition.create("test-topic-1", 0),
            KafkaTopicPartition.create("test-topic-2", 1)
        ));
        Set<TopicPartition> vertxTopicPartitions = new HashSet<>(Arrays.asList(
            new TopicPartition("test-topic-1", 0),
            new TopicPartition("test-topic-2", 1)
        ));

        given(mockAxleKafkaConsumer.assign(vertxTopicPartitions)).willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(consumer.assign(kafkaTopicPartitions))
            .verifyComplete();
    }

    @Test
    public void shouldGetAssignment() {
        Set<TopicPartition> vertxTopicPartitions = new HashSet<>(Arrays.asList(
            new TopicPartition("test-topic-1", 0),
            new TopicPartition("test-topic-2", 1)
        ));
        given(mockAxleKafkaConsumer.assignment()).willReturn(CompletableFuture.completedFuture(vertxTopicPartitions));

        StepVerifier.create(consumer.assignment())
            .expectNext(KafkaTopicPartition.create("test-topic-1", 0))
            .expectNext(KafkaTopicPartition.create("test-topic-2", 1))
            .verifyComplete();
    }

    @Test
    public void shouldUnsubscribe() {
        given(mockAxleKafkaConsumer.unsubscribe()).willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(consumer.unsubscribe())
            .verifyComplete();
    }

    @Test
    public void shouldGetSubscriptions() {
        Set<String> topics = new HashSet<>(Arrays.asList("test-topic-1", "test-topic-2"));
        given(mockAxleKafkaConsumer.subscription()).willReturn(CompletableFuture.completedFuture(topics));

        StepVerifier.create(consumer.subscription())
            .expectNextSequence(topics)
            .verifyComplete();
    }

    @Test
    public void shouldPauseSinglePartition() {
        given(mockAxleKafkaConsumer.pause(new TopicPartition("test-topic", 1)))
            .willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(consumer.pause(KafkaTopicPartition.create("test-topic", 1)))
            .verifyComplete();
    }

    @Test
    public void shouldPauseMultiplePartitions() {
        Set<KafkaTopicPartition> kafkaTopicPartitions = new HashSet<>(Arrays.asList(
            KafkaTopicPartition.create("test-topic-1", 0),
            KafkaTopicPartition.create("test-topic-2", 1)
        ));
        Set<TopicPartition> vertxTopicPartitions = new HashSet<>(Arrays.asList(
            new TopicPartition("test-topic-1", 0),
            new TopicPartition("test-topic-2", 1)
        ));
        given(mockAxleKafkaConsumer.pause(vertxTopicPartitions))
            .willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(consumer.pause(kafkaTopicPartitions))
            .verifyComplete();
    }

    @Test
    public void shouldGetPausedPartitions() {
        Set<TopicPartition> vertxTopicPartitions = new HashSet<>(Arrays.asList(
            new TopicPartition("test-topic-1", 0),
            new TopicPartition("test-topic-2", 1)
        ));
        given(mockAxleKafkaConsumer.paused()).willReturn(CompletableFuture.completedFuture(vertxTopicPartitions));

        StepVerifier.create(consumer.paused())
            .expectNext(KafkaTopicPartition.create("test-topic-1", 0))
            .expectNext(KafkaTopicPartition.create("test-topic-2", 1))
            .verifyComplete();
    }

    @Test
    public void shouldResumeSinglePartition() {
        given(mockAxleKafkaConsumer.resume(new TopicPartition("test-topic", 1)))
            .willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(consumer.resume(KafkaTopicPartition.create("test-topic", 1)))
            .verifyComplete();
    }

    @Test
    public void shouldResumeMultiplePartitions() {
        Set<KafkaTopicPartition> kafkaTopicPartitions = new HashSet<>(Arrays.asList(
            KafkaTopicPartition.create("test-topic-1", 0),
            KafkaTopicPartition.create("test-topic-2", 1)
        ));
        Set<TopicPartition> vertxTopicPartitions = new HashSet<>(Arrays.asList(
            new TopicPartition("test-topic-1", 0),
            new TopicPartition("test-topic-2", 1)
        ));
        given(mockAxleKafkaConsumer.resume(vertxTopicPartitions))
            .willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(consumer.resume(kafkaTopicPartitions))
            .verifyComplete();
    }

    @Test
    public void shouldAddPartitionsRevokedHandler() {
        given(mockAxleKafkaConsumer.partitionsRevokedHandler(any()))
            .will(a -> {
                Consumer<Set<TopicPartition>> handler = a.getArgument(0);
                handler.accept(Collections.singleton(new TopicPartition("test-topic", 1)));
                return mockAxleKafkaConsumer;
            });

        AtomicReference<Flux<KafkaTopicPartition>> futureTopicPartition = new AtomicReference<>();
        consumer.partitionsRevokedHandler(futureTopicPartition::set);

        await().atMost(Duration.ofSeconds(2))
            .untilAtomic(futureTopicPartition, Matchers.is(Matchers.notNullValue()));

        StepVerifier.create(futureTopicPartition.get())
            .expectNext(KafkaTopicPartition.create("test-topic", 1))
            .verifyComplete();
    }

    @Test
    public void shouldAddPartitionsAssignedHandler() {
        given(mockAxleKafkaConsumer.partitionsAssignedHandler(any()))
            .will(a -> {
                Consumer<Set<TopicPartition>> handler = a.getArgument(0);
                handler.accept(Collections.singleton(new TopicPartition("test-topic", 1)));
                return mockAxleKafkaConsumer;
            });

        AtomicReference<Flux<KafkaTopicPartition>> futureTopicPartition = new AtomicReference<>();
        consumer.partitionsAssignedHandler(futureTopicPartition::set);

        await().atMost(Duration.ofSeconds(2))
            .untilAtomic(futureTopicPartition, Matchers.is(Matchers.notNullValue()));

        StepVerifier.create(futureTopicPartition.get())
            .expectNext(KafkaTopicPartition.create("test-topic", 1))
            .verifyComplete();
    }

    @Test
    public void shouldSeek() {
        given(mockAxleKafkaConsumer.seek(new TopicPartition("test-topic", 1), 2))
            .willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(consumer.seek(KafkaTopicPartition.create("test-topic", 1), 2))
            .verifyComplete();
    }

    @Test
    public void shouldSeekToBeginningOfSinglePartition() {
        given(mockAxleKafkaConsumer.seekToBeginning(new TopicPartition("test-topic", 1)))
            .willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(consumer.seekToBeginning(KafkaTopicPartition.create("test-topic", 1)))
            .verifyComplete();
    }

    @Test
    public void shouldSeekToBeginningOfMultiplePartitions() {
        given(mockAxleKafkaConsumer.seekToBeginning(Collections.singleton(new TopicPartition("test-topic", 1))))
            .willReturn(CompletableFuture.completedFuture(null));

        StepVerifier
            .create(consumer.seekToBeginning(Collections.singleton(KafkaTopicPartition.create("test-topic", 1))))
            .verifyComplete();
    }

    @Test
    public void shouldSeekToEndOfSinglePartition() {
        given(mockAxleKafkaConsumer.seekToEnd(new TopicPartition("test-topic", 1)))
            .willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(consumer.seekToEnd(KafkaTopicPartition.create("test-topic", 1)))
            .verifyComplete();
    }

    @Test
    public void shouldSeekToEndOfMultiplePartitions() {
        given(mockAxleKafkaConsumer.seekToEnd(Collections.singleton(new TopicPartition("test-topic", 1))))
            .willReturn(CompletableFuture.completedFuture(null));

        StepVerifier
            .create(consumer.seekToEnd(Collections.singleton(KafkaTopicPartition.create("test-topic", 1))))
            .verifyComplete();
    }

    @Test
    public void shouldCommit() {
        given(mockAxleKafkaConsumer.commit())
            .willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(consumer.commit())
            .verifyComplete();
    }

    @Test
    public void shouldGetCommittedOffset() {
        given(mockAxleKafkaConsumer.committed(new TopicPartition("test-topic", 1)))
            .willReturn(CompletableFuture.completedFuture(new OffsetAndMetadata(2, "test-metadata")));

        StepVerifier.create(consumer.committed(KafkaTopicPartition.create("test-topic", 1)))
            .expectNext(new SnowdropKafkaOffsetAndMetadata(2, "test-metadata"))
            .verifyComplete();
    }

    @Test
    public void getPartitionsFor() {
        List<Node> vertxKafkaNodes = Arrays.asList(
            new Node(true, "test-host", 1, "1", true, 8080, "test-rack"),
            new Node(true, "test-host", 2, "2", true, 8080, "test-rack")
        );
        PartitionInfo vertxKafkaPartitionInfo =
            new PartitionInfo(vertxKafkaNodes, vertxKafkaNodes.get(0), 1, vertxKafkaNodes, "test-topic");

        given(mockAxleKafkaConsumer.partitionsFor("test-topic")).willReturn(
            CompletableFuture.completedFuture(Collections.singletonList(vertxKafkaPartitionInfo)));

        StepVerifier.create(consumer.partitionsFor("test-topic"))
            .expectNext(new SnowdropKafkaPartitionInfo(vertxKafkaPartitionInfo))
            .verifyComplete();
    }

    @Test
    public void shouldAddBatchHandler() {
        given(mockAxleKafkaConsumer.batchHandler(any()))
            .will(a -> {
                Consumer<io.vertx.axle.kafka.client.consumer.KafkaConsumerRecords> handler = a.getArgument(0);
                handler.accept(mockAxleKafkaConsumerRecords);
                return mockAxleKafkaConsumer;
            });

        AtomicReference<KafkaConsumerRecords<Integer, String>> futureRecords = new AtomicReference<>();
        consumer.batchHandler(futureRecords::set);

        await().atMost(Duration.ofSeconds(2))
            .untilAtomic(futureRecords, Matchers.is(Matchers.notNullValue()));

        assertThat(futureRecords.get()).isEqualTo(new SnowdropKafkaConsumerRecords<>(mockAxleKafkaConsumerRecords));
    }

    @Test
    public void shouldClose() {
        given(mockAxleKafkaConsumer.close())
            .willReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(consumer.close())
            .verifyComplete();
    }

    @Test
    public void shouldGetPosition() {
        given(mockAxleKafkaConsumer.position(new TopicPartition("test-topic", 1)))
            .willReturn(CompletableFuture.completedFuture(1L));

        StepVerifier.create(consumer.position(KafkaTopicPartition.create("test-topic", 1)))
            .expectNext(1L)
            .verifyComplete();
    }

    @Test
    public void shouldGetOffsetForTimes() {
        io.vertx.kafka.client.consumer.OffsetAndTimestamp vertxOffsetAndTimestamp =
            new io.vertx.kafka.client.consumer.OffsetAndTimestamp(3L, 4L);
        given(mockAxleKafkaConsumer.offsetsForTimes(new TopicPartition("test-topic", 1), 2L))
            .willReturn(CompletableFuture.completedFuture(vertxOffsetAndTimestamp));

        StepVerifier.create(consumer.offsetsForTimes(KafkaTopicPartition.create("test-topic", 1), 2L))
            .expectNext(new SnowdropKafkaOffsetAndTimestamp(vertxOffsetAndTimestamp))
            .verifyComplete();
    }

    @Test
    public void shouldGetBeginningOffset() {
        given(mockAxleKafkaConsumer.beginningOffsets(new TopicPartition("test-topic", 1)))
            .willReturn(CompletableFuture.completedFuture(2L));

        StepVerifier.create(consumer.beginningOffsets(KafkaTopicPartition.create("test-topic", 1)))
            .expectNext(2L)
            .verifyComplete();
    }

    @Test
    public void shouldGetEndOffset() {
        given(mockAxleKafkaConsumer.endOffsets(new TopicPartition("test-topic", 1)))
            .willReturn(CompletableFuture.completedFuture(2L));

        StepVerifier.create(consumer.endOffsets(KafkaTopicPartition.create("test-topic", 1)))
            .expectNext(2L)
            .verifyComplete();
    }

    @Test
    public void shouldSetPollTimeout() {
        consumer.pollTimeout(1);

        verify(mockAxleKafkaConsumer).pollTimeout(1);
    }

    @Test
    public void shouldPollRecords() {
        given(mockAxleKafkaConsumer.poll(1L))
            .willReturn(CompletableFuture.completedFuture(mockAxleKafkaConsumerRecords));

        StepVerifier.create(consumer.poll(1))
            .expectNext(new SnowdropKafkaConsumerRecords<>(mockAxleKafkaConsumerRecords))
            .verifyComplete();
    }

    @Test
    public void shouldGetMono() {
        given(mockAxleKafkaConsumer.toPublisher()).willReturn(Flux.just(mockAxleKafkaConsumerRecord));

        StepVerifier.create(consumer.mono())
            .expectNext(new SnowdropKafkaConsumerRecord<>(mockAxleKafkaConsumerRecord))
            .verifyComplete();
    }

    @Test
    public void shouldGetFlux() {
        given(mockAxleKafkaConsumer.toPublisher()).willReturn(Flux.just(mockAxleKafkaConsumerRecord));

        StepVerifier.create(consumer.flux())
            .expectNext(new SnowdropKafkaConsumerRecord<>(mockAxleKafkaConsumerRecord))
            .verifyComplete();
    }

    @Test
    public void shouldGetVertxReadStream() {
        given(mockAxleKafkaConsumer.getDelegate()).willReturn(mockVertxKafkaConsumer);

        assertThat(consumer.vertxReadStream()).isEqualTo(mockVertxKafkaConsumer);
    }
}
