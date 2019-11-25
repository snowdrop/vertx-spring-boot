package dev.snowdrop.vertx.kafka;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import dev.snowdrop.vertx.streams.ReadStream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface KafkaConsumer<K, V> extends ReadStream<ConsumerRecord<K, V>> {

    Mono<Void> subscribe(String topic);

    Mono<Void> subscribe(Collection<String> topics);

    Mono<Void> assign(Partition partition);

    Mono<Void> assign(Collection<Partition> partitions);

    Mono<Void> unsubscribe();

    Flux<String> subscriptions();

    Flux<Partition> assignments();

    Flux<PartitionInfo> partitionsFor(String topic);

    void partitionsRevokedHandler(Consumer<Set<Partition>> handler);

    void partitionsAssignedHandler(Consumer<Set<Partition>> handler);

    Mono<Void> seek(Partition partition, long offset);

    Mono<Void> seekToBeginning(Partition partition);

    Mono<Void> seekToBeginning(Collection<Partition> partitions);

    Mono<Void> seekToEnd(Partition partition);

    Mono<Void> seekToEnd(Collection<Partition> partitions);

    Mono<Long> position(Partition partition);

    Mono<Long> committed(Partition partition);

    Mono<Long> beginningOffset(Partition partition);

    Mono<Long> endOffset(Partition partition);

    Mono<Long> timeOffset(Partition partition, long timestamp);

    Mono<Void> commit();

    Mono<Void> close();

    <T> Mono<T> doOnVertxConsumer(Function<io.vertx.kafka.client.consumer.KafkaConsumer<K, V>, T> function);
}
