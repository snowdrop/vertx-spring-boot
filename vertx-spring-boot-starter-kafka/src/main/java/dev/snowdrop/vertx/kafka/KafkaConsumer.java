package dev.snowdrop.vertx.kafka;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import dev.snowdrop.vertx.streams.ReadStream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface KafkaConsumer<K, V> extends ReadStream<KafkaConsumerRecord<K, V>> {

    Mono<Void> subscribe(String topic);

    Mono<Void> subscribe(Set<String> topics);

    Mono<Void> subscribe(Pattern pattern);

    Mono<Void> assign(TopicPartition partition);

    Mono<Void> assign(Set<TopicPartition> partitions);

    Flux<TopicPartition> assignment();

    Flux<KafkaPartitionInfo> listTopics();

    Mono<Void> unsubscribe();

    Flux<String> subscription();

    Mono<Void> pause(TopicPartition partition);

    Mono<Void> pause(Set<TopicPartition> partitions);

    Flux<TopicPartition> paused();

    Mono<Void> resume(TopicPartition partition);

    Mono<Void> resume(Set<TopicPartition> partitions);

    void partitionsRevokedHandler(Consumer<Flux<TopicPartition>> handler);

    void partitionsAssignedHandler(Consumer<Flux<TopicPartition>> handler);

    Mono<Void> seek(TopicPartition partition, long offset);

    Mono<Void> seekToBeginning(TopicPartition partition);

    Mono<Void> seekToBeginning(Set<TopicPartition> partitions);

    Mono<Void> seekToEnd(TopicPartition partition);

    Mono<Void> seekToEnd(Set<TopicPartition> partitions);

    Mono<Void> commit();

    Mono<Map<TopicPartition, OffsetAndMetadata>> commit(Map<TopicPartition, OffsetAndMetadata> offsets);

    Mono<OffsetAndMetadata> committed(TopicPartition topicPartition);

    Flux<KafkaPartitionInfo> partitionsFor(String topic);

    void batchHandler(Consumer<KafkaConsumerRecords<K, V>> handler);

    Mono<Void> close();

    Mono<Long> position(TopicPartition partition);

    Mono<Map<TopicPartition, OffsetAndTimestamp>> offsetsForTimes(Map<TopicPartition, Long> partitionTimestamps);

    Mono<OffsetAndTimestamp> offsetsForTimes(TopicPartition partition, Long timestamp);

    Mono<Long> beginningOffsets(TopicPartition partition);

    Mono<Map<TopicPartition, Long>> beginningOffsets(Set<TopicPartition> partitions);

    Mono<Long> endOffsets(TopicPartition partition);

    Mono<Map<TopicPartition, Long>> endOffsets(Set<TopicPartition> partitions);

    void pollTimeout(long timeout);

    Mono<KafkaConsumerRecords<K, V>> poll(long timeout);

    org.apache.kafka.clients.consumer.Consumer<K, V> unwrap();
}
