package dev.snowdrop.vertx.kafka;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.smallrye.mutiny.converters.multi.MultiReactorConverters;
import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import io.vertx.core.streams.ReadStream;
import io.vertx.kafka.client.common.TopicPartition;
import io.vertx.kafka.client.consumer.OffsetAndMetadata;
import io.vertx.kafka.client.consumer.OffsetAndTimestamp;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

final class SnowdropKafkaConsumer<K, V> implements KafkaConsumer<K, V> {

    private final io.vertx.mutiny.kafka.client.consumer.KafkaConsumer<K, V> delegate;

    SnowdropKafkaConsumer(io.vertx.mutiny.kafka.client.consumer.KafkaConsumer<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Mono<Void> subscribe(String topic) {
        if (StringUtils.isEmpty(topic)) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }

        return delegate.subscribe(topic).convert().with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Void> subscribe(Collection<String> topics) {
        Objects.requireNonNull(topics, "Topics cannot be null");

        return delegate.subscribe(new HashSet<>(topics)).convert().with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Void> assign(Partition partition) {
        Objects.requireNonNull(partition, "Partition cannot be null");

        return delegate.assign(toVertxTopicPartition(partition)).convert().with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Void> assign(Collection<Partition> partitions) {
        Objects.requireNonNull(partitions, "Partitions cannot be null");

        Set<TopicPartition> vertxPartitions = partitions
            .stream()
            .map(this::toVertxTopicPartition)
            .collect(Collectors.toSet());

        return delegate.assign(vertxPartitions).convert().with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Void> unsubscribe() {
        return delegate.unsubscribe().convert().with(UniReactorConverters.toMono());
    }

    @Override
    public Flux<String> subscriptions() {
        return UniReactorConverters.<Set<String>>toMono().apply(delegate.subscription())
            .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Flux<Partition> assignments() {
        return delegate.assignment()
            .convert()
            .with(UniReactorConverters.toMono())
            .flatMapMany(Flux::fromIterable)
            .map(SnowdropPartition::new);
    }

    @Override
    public Flux<PartitionInfo> partitionsFor(String topic) {
        if (StringUtils.isEmpty(topic)) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }

        return delegate.partitionsFor(topic)
            .convert()
            .with(UniReactorConverters.toMono())
            .flatMapMany(Flux::fromIterable)
            .map(SnowdropPartitionInfo::new);
    }

    @Override
    public void partitionsRevokedHandler(Consumer<Set<Partition>> handler) {
        Objects.requireNonNull(handler, "Handler cannot be null");

        delegate.partitionsRevokedHandler(mutinyTopicPartitions -> {
            Set<Partition> partitions = mutinyTopicPartitions
                .stream()
                .map(SnowdropPartition::new)
                .collect(Collectors.toSet());

            handler.accept(partitions);
        });
    }

    @Override
    public void partitionsAssignedHandler(Consumer<Set<Partition>> handler) {
        Objects.requireNonNull(handler, "Handler cannot be null");

        delegate.partitionsAssignedHandler(mutinyTopicPartitions -> {
            Set<Partition> partitions = mutinyTopicPartitions
                .stream()
                .map(SnowdropPartition::new)
                .collect(Collectors.toSet());

            handler.accept(partitions);
        });
    }

    @Override
    public Mono<Void> seek(Partition partition, long offset) {
        Objects.requireNonNull(partition, "Partition cannot be null");
        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative");
        }

        return delegate.seek(toVertxTopicPartition(partition), offset).convert().with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Void> seekToBeginning(Partition partition) {
        Objects.requireNonNull(partition, "Partition cannot be null");

        return delegate.seekToBeginning(toVertxTopicPartition(partition)).convert().with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Void> seekToBeginning(Collection<Partition> partitions) {
        Objects.requireNonNull(partitions, "Partitions cannot be null");

        Set<TopicPartition> vertxPartitions = partitions
            .stream()
            .map(this::toVertxTopicPartition)
            .collect(Collectors.toSet());

        return delegate.seekToBeginning(vertxPartitions).convert().with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Void> seekToEnd(Partition partition) {
        Objects.requireNonNull(partition, "Partition cannot be null");

        return delegate.seekToEnd(toVertxTopicPartition(partition)).convert().with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Void> seekToEnd(Collection<Partition> partitions) {
        Objects.requireNonNull(partitions, "Partitions cannot be null");

        Set<TopicPartition> vertxPartitions = partitions
            .stream()
            .map(this::toVertxTopicPartition)
            .collect(Collectors.toSet());

        return delegate.seekToEnd(vertxPartitions).convert().with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Long> position(Partition partition) {
        Objects.requireNonNull(partition, "Partition cannot be null");

        return delegate.position(toVertxTopicPartition(partition)).convert().with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Long> committed(Partition partition) {
        Objects.requireNonNull(partition, "Partition cannot be null");

        return delegate.committed(toVertxTopicPartition(partition))
            .convert()
            .with(UniReactorConverters.toMono())
            .map(OffsetAndMetadata::getOffset);
    }

    @Override
    public Mono<Long> beginningOffset(Partition partition) {
        Objects.requireNonNull(partition, "Partition cannot be null");

        return delegate.beginningOffsets(toVertxTopicPartition(partition))
            .convert()
            .with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Long> endOffset(Partition partition) {
        Objects.requireNonNull(partition, "Partition cannot be null");

        return delegate.endOffsets(toVertxTopicPartition(partition)).convert().with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Long> timeOffset(Partition partition, long timestamp) {
        Objects.requireNonNull(partition, "Partition cannot be null");
        if (timestamp < 0) {
            throw new IllegalArgumentException("Timestamp cannot be negative");
        }

        return delegate.offsetsForTimes(toVertxTopicPartition(partition), timestamp)
            .convert()
            .with(UniReactorConverters.toMono())
            .map(OffsetAndTimestamp::getOffset);
    }

    @Override
    public Mono<Void> commit() {
        return delegate.commit().convert().with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Void> close() {
        return delegate.close().convert().with(UniReactorConverters.toMono());
    }

    @Override
    @SuppressWarnings("unchecked") // SmallRye API returns KafkaConsumer without generics
    public <T> Mono<T> doOnVertxConsumer(Function<io.vertx.kafka.client.consumer.KafkaConsumer<K, V>, T> function) {
        Objects.requireNonNull(function, "Function cannot be null");

        return Mono.create(sink -> {
            try {
                T result = function.apply((io.vertx.kafka.client.consumer.KafkaConsumer<K, V>) delegate.getDelegate());
                sink.success(result);
            } catch (Throwable t) {
                sink.error(t);
            }
        });
    }

    @Override
    public Mono<ConsumerRecord<K, V>> mono() {
        return delegate.toMulti()
            .convert()
            .with(MultiReactorConverters.toMono())
            .map(SnowdropConsumerRecord::new);
    }

    @Override
    public Flux<ConsumerRecord<K, V>> flux() {
        return delegate.toMulti()
            .convert()
            .with(MultiReactorConverters.toFlux())
            .map(SnowdropConsumerRecord::new);
    }

    @Override
    public ReadStream vertxReadStream() {
        return delegate.getDelegate();
    }

    private TopicPartition toVertxTopicPartition(Partition partition) {
        return new TopicPartition(partition.topic(), partition.partition());
    }
}
