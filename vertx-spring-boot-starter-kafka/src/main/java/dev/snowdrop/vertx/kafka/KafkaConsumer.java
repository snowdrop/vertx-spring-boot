package dev.snowdrop.vertx.kafka;

import java.util.Set;
import java.util.function.Consumer;

import dev.snowdrop.vertx.streams.ReadStream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface KafkaConsumer<K, V> extends ReadStream<KafkaConsumerRecord<K, V>> {

    Mono<Void> subscribe(String topic);

    Mono<Void> subscribe(Set<String> topics);

    Mono<Void> assign(KafkaTopicPartition partition);

    Mono<Void> assign(Set<KafkaTopicPartition> partitions);

    Flux<KafkaTopicPartition> assignment();

    Mono<Void> unsubscribe();

    Flux<String> subscription();

    Mono<Void> pause(KafkaTopicPartition partition);

    Mono<Void> pause(Set<KafkaTopicPartition> partitions);

    Flux<KafkaTopicPartition> paused();

    Mono<Void> resume(KafkaTopicPartition partition);

    Mono<Void> resume(Set<KafkaTopicPartition> partitions);

    void partitionsRevokedHandler(Consumer<Flux<KafkaTopicPartition>> handler);

    void partitionsAssignedHandler(Consumer<Flux<KafkaTopicPartition>> handler);

    Mono<Void> seek(KafkaTopicPartition partition, long offset);

    Mono<Void> seekToBeginning(KafkaTopicPartition partition);

    Mono<Void> seekToBeginning(Set<KafkaTopicPartition> partitions);

    Mono<Void> seekToEnd(KafkaTopicPartition partition);

    Mono<Void> seekToEnd(Set<KafkaTopicPartition> partitions);

    Mono<Void> commit();

    Mono<KafkaOffsetAndMetadata> committed(KafkaTopicPartition topicPartition);

    Flux<KafkaPartitionInfo> partitionsFor(String topic);

    void batchHandler(Consumer<KafkaConsumerRecords<K, V>> handler);

    Mono<Void> close();

    Mono<Long> position(KafkaTopicPartition partition);

    Mono<KafkaOffsetAndTimestamp> offsetsForTimes(KafkaTopicPartition partition, Long timestamp);

    Mono<Long> beginningOffsets(KafkaTopicPartition partition);

    Mono<Long> endOffsets(KafkaTopicPartition partition);

    void pollTimeout(long timeout);

    Mono<KafkaConsumerRecords<K, V>> poll(long timeout);
}
