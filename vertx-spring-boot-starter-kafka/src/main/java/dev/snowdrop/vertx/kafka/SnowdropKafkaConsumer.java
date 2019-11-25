package dev.snowdrop.vertx.kafka;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.vertx.core.streams.ReadStream;
import io.vertx.kafka.client.common.TopicPartition;
import io.vertx.kafka.client.consumer.OffsetAndMetadata;
import io.vertx.kafka.client.consumer.OffsetAndTimestamp;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

final class SnowdropKafkaConsumer<K, V> implements KafkaConsumer<K, V> {

    private final io.vertx.axle.kafka.client.consumer.KafkaConsumer<K, V> delegate;

    SnowdropKafkaConsumer(io.vertx.axle.kafka.client.consumer.KafkaConsumer<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Mono<Void> subscribe(String topic) {
        if (StringUtils.isEmpty(topic)) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }

        return Mono.fromCompletionStage(() -> delegate.subscribe(topic));
    }

    @Override
    public Mono<Void> subscribe(Flux<String> topics) {
        Objects.requireNonNull(topics, "Topics cannot be null");

        Set<String> topicsSet = topics
            .collect(Collectors.toSet())
            .block();

        return Mono.fromCompletionStage(() -> delegate.subscribe(topicsSet));
    }

    @Override
    public Mono<Void> assign(Partition partition) {
        Objects.requireNonNull(partition, "Partition cannot be null");

        return Mono.fromCompletionStage(() -> delegate.assign(toVertxTopicPartition(partition)));
    }

    @Override
    public Mono<Void> assign(Flux<Partition> partitions) {
        Objects.requireNonNull(partitions, "Partitions cannot be null");

        Set<TopicPartition> vertxPartitions = partitions
            .map(this::toVertxTopicPartition)
            .collect(Collectors.toSet())
            .block();

        return Mono.fromCompletionStage(() -> delegate.assign(vertxPartitions));
    }

    @Override
    public Mono<Void> unsubscribe() {
        return Mono.fromCompletionStage(delegate::unsubscribe);
    }

    @Override
    public Flux<String> subscriptions() {
        return Mono.fromCompletionStage(delegate::subscription)
            .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Flux<Partition> assignments() {
        return Mono.fromCompletionStage(delegate::assignment)
            .flatMapMany(Flux::fromIterable)
            .map(SnowdropPartition::new);
    }

    @Override
    public Flux<PartitionInfo> partitionsFor(String topic) {
        if (StringUtils.isEmpty(topic)) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }

        return Mono.fromCompletionStage(() -> delegate.partitionsFor(topic))
            .flatMapMany(Flux::fromIterable)
            .map(SnowdropPartitionInfo::new);
    }

    @Override
    public void partitionsRevokedHandler(Consumer<Flux<Partition>> handler) {
        Objects.requireNonNull(handler, "Handler cannot be null");

        delegate.partitionsRevokedHandler(axleTopicPartitions -> {
            Flux<Partition> partitions = Flux
                .fromIterable(axleTopicPartitions)
                .map(SnowdropPartition::new);

            handler.accept(partitions);
        });
    }

    @Override
    public void partitionsAssignedHandler(Consumer<Flux<Partition>> handler) {
        Objects.requireNonNull(handler, "Handler cannot be null");

        delegate.partitionsAssignedHandler(axleTopicPartitions -> {
            Flux<Partition> partitions = Flux
                .fromIterable(axleTopicPartitions)
                .map(SnowdropPartition::new);

            handler.accept(partitions);
        });
    }

    @Override
    public Mono<Void> seek(Partition partition, long offset) {
        Objects.requireNonNull(partition, "Partition cannot be null");
        if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative");
        }

        return Mono.fromCompletionStage(() -> delegate.seek(toVertxTopicPartition(partition), offset));
    }

    @Override
    public Mono<Void> seekToBeginning(Partition partition) {
        Objects.requireNonNull(partition, "Partition cannot be null");

        return Mono.fromCompletionStage(() -> delegate.seekToBeginning(toVertxTopicPartition(partition)));
    }

    @Override
    public Mono<Void> seekToBeginning(Flux<Partition> partitions) {
        Objects.requireNonNull(partitions, "Partitions cannot be null");

        Set<TopicPartition> vertxPartitions = partitions
            .map(this::toVertxTopicPartition)
            .collect(Collectors.toSet())
            .block();

        return Mono.fromCompletionStage(() -> delegate.seekToBeginning(vertxPartitions));
    }

    @Override
    public Mono<Void> seekToEnd(Partition partition) {
        Objects.requireNonNull(partition, "Partition cannot be null");

        return Mono.fromCompletionStage(() -> delegate.seekToEnd(toVertxTopicPartition(partition)));
    }

    @Override
    public Mono<Void> seekToEnd(Flux<Partition> partitions) {
        Objects.requireNonNull(partitions, "Partitions cannot be null");

        Set<TopicPartition> vertxPartitions = partitions
            .map(this::toVertxTopicPartition)
            .collect(Collectors.toSet())
            .block();

        return Mono.fromCompletionStage(() -> delegate.seekToEnd(vertxPartitions));
    }

    @Override
    public Mono<Long> position(Partition partition) {
        Objects.requireNonNull(partition, "Partition cannot be null");

        return Mono.fromCompletionStage(() -> delegate.position(toVertxTopicPartition(partition)));
    }

    @Override
    public Mono<Long> committed(Partition partition) {
        Objects.requireNonNull(partition, "Partition cannot be null");

        return Mono.fromCompletionStage(() -> delegate.committed(toVertxTopicPartition(partition)))
            .map(OffsetAndMetadata::getOffset);
    }

    @Override
    public Mono<Long> beginningOffset(Partition partition) {
        Objects.requireNonNull(partition, "Partition cannot be null");

        return Mono.fromCompletionStage(() -> delegate.beginningOffsets(toVertxTopicPartition(partition)));
    }

    @Override
    public Mono<Long> endOffset(Partition partition) {
        Objects.requireNonNull(partition, "Partition cannot be null");

        return Mono.fromCompletionStage(() -> delegate.endOffsets(toVertxTopicPartition(partition)));
    }

    @Override
    public Mono<Long> timeOffset(Partition partition, long timestamp) {
        Objects.requireNonNull(partition, "Partition cannot be null");
        if (timestamp < 0) {
            throw new IllegalArgumentException("Timestamp cannot be negative");
        }

        return Mono.fromCompletionStage(() -> delegate.offsetsForTimes(toVertxTopicPartition(partition), timestamp))
            .map(OffsetAndTimestamp::getOffset);
    }

    @Override
    public Mono<Void> commit() {
        return Mono.fromCompletionStage(delegate::commit);
    }

    @Override
    public Mono<Void> close() {
        return Mono.fromCompletionStage(delegate::close);
    }

    @Override
    @SuppressWarnings("unchecked") // Axle API returns KafkaConsumer without generics
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
        return Mono.from(delegate.toPublisher())
            .map(SnowdropConsumerRecord::new);
    }

    @Override
    public Flux<ConsumerRecord<K, V>> flux() {
        return Flux.from(delegate.toPublisher())
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
