package dev.snowdrop.vertx.kafka;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.snowdrop.vertx.streams.ReadStream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface KafkaConsumer<K, V> extends ReadStream<ConsumerRecord<K, V>> {

    Mono<Void> subscribe(String topic);

    Mono<Void> subscribe(Flux<String> topics);

    Mono<Void> assign(KafkaTopicPartition partition);

    Mono<Void> assign(Flux<KafkaTopicPartition> partitions);

    Mono<Void> unsubscribe();

    Flux<String> subscriptions();

    Flux<KafkaTopicPartition> assignments();

    Flux<KafkaPartitionInfo> partitionsFor(String topic);

    void partitionsRevokedHandler(Consumer<Flux<KafkaTopicPartition>> handler);

    void partitionsAssignedHandler(Consumer<Flux<KafkaTopicPartition>> handler);

    Mono<Void> seek(KafkaTopicPartition partition, long offset);

    Mono<Void> seekToBeginning(KafkaTopicPartition partition);

    Mono<Void> seekToBeginning(Flux<KafkaTopicPartition> partitions);

    Mono<Void> seekToEnd(KafkaTopicPartition partition);

    Mono<Void> seekToEnd(Flux<KafkaTopicPartition> partitions);

    Mono<Long> position(KafkaTopicPartition partition);

    Mono<Long> committed(KafkaTopicPartition partition);

    Mono<Long> beginningOffset(KafkaTopicPartition partition);

    Mono<Long> endOffset(KafkaTopicPartition partition);

    Mono<Long> timeOffset(KafkaTopicPartition partition, long timestamp);

    Mono<Void> commit();

    Mono<Void> close();

    <T> Mono<T> doOnVertxConsumer(Function<io.vertx.kafka.client.consumer.KafkaConsumer<K, V>, T> function);
}
