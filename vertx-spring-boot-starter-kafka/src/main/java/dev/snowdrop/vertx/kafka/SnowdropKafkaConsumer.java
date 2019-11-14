package dev.snowdrop.vertx.kafka;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.vertx.core.streams.ReadStream;
import io.vertx.kafka.client.common.TopicPartition;
import io.vertx.kafka.client.consumer.OffsetAndMetadata;
import io.vertx.kafka.client.consumer.OffsetAndTimestamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// TODO make package-private once Spring configuration is setup
public final class SnowdropKafkaConsumer<K, V> implements KafkaConsumer<K, V> {

    private final io.vertx.axle.kafka.client.consumer.KafkaConsumer<K, V> delegate;

    // TODO make package-private once Spring configuration is setup
    public SnowdropKafkaConsumer(io.vertx.axle.kafka.client.consumer.KafkaConsumer<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Mono<Void> subscribe(String topic) {
        return Mono.fromCompletionStage(delegate.subscribe(topic));
    }

    @Override
    public Mono<Void> subscribe(Flux<String> topics) {
        Set<String> topicsSet = topics
            .collect(Collectors.toSet())
            .block();

        return Mono.fromCompletionStage(delegate.subscribe(topicsSet));
    }

    @Override
    public Mono<Void> assign(KafkaTopicPartition partition) {
        return Mono.fromCompletionStage(delegate.assign(partition.toVertxTopicPartition()));
    }

    @Override
    public Mono<Void> assign(Flux<KafkaTopicPartition> partitions) {
        Set<TopicPartition> vertxPartitions = partitions
            .map(KafkaTopicPartition::toVertxTopicPartition)
            .collect(Collectors.toSet())
            .block();

        return Mono.fromCompletionStage(delegate.assign(vertxPartitions));
    }

    @Override
    public Mono<Void> unsubscribe() {
        return Mono.fromCompletionStage(delegate.unsubscribe());
    }

    @Override
    public Flux<String> subscriptions() {
        return Mono.fromCompletionStage(delegate.subscription())
            .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Flux<KafkaTopicPartition> assignments() {
        return Mono.fromCompletionStage(delegate.assignment())
            .flatMapMany(Flux::fromIterable)
            .map(KafkaTopicPartition::create);
    }

    @Override
    public Flux<KafkaPartitionInfo> partitionsFor(String topic) {
        return Mono.fromCompletionStage(delegate.partitionsFor(topic))
            .flatMapMany(Flux::fromIterable)
            .map(SnowdropKafkaPartitionInfo::new);
    }

    @Override
    public void partitionsRevokedHandler(Consumer<Flux<KafkaTopicPartition>> handler) {
        delegate.partitionsRevokedHandler(axleTopicPartitions -> {
            Flux<KafkaTopicPartition> kafkaTopicPartitions = Flux
                .fromIterable(axleTopicPartitions)
                .map(KafkaTopicPartition::create);

            handler.accept(kafkaTopicPartitions);
        });
    }

    @Override
    public void partitionsAssignedHandler(Consumer<Flux<KafkaTopicPartition>> handler) {
        delegate.partitionsAssignedHandler(axleTopicPartitions -> {
            Flux<KafkaTopicPartition> kafkaTopicPartitions = Flux
                .fromIterable(axleTopicPartitions)
                .map(KafkaTopicPartition::create);

            handler.accept(kafkaTopicPartitions);
        });
    }

    @Override
    public Mono<Void> seek(KafkaTopicPartition partition, long offset) {
        return Mono.fromCompletionStage(delegate.seek(partition.toVertxTopicPartition(), offset));
    }

    @Override
    public Mono<Void> seekToBeginning(KafkaTopicPartition partition) {
        return Mono.fromCompletionStage(delegate.seekToBeginning(partition.toVertxTopicPartition()));
    }

    @Override
    public Mono<Void> seekToBeginning(Flux<KafkaTopicPartition> partitions) {
        Set<TopicPartition> vertxPartitions = partitions
            .map(KafkaTopicPartition::toVertxTopicPartition)
            .collect(Collectors.toSet())
            .block();

        return Mono.fromCompletionStage(delegate.seekToBeginning(vertxPartitions));
    }

    @Override
    public Mono<Void> seekToEnd(KafkaTopicPartition partition) {
        return Mono.fromCompletionStage(delegate.seekToEnd(partition.toVertxTopicPartition()));
    }

    @Override
    public Mono<Void> seekToEnd(Flux<KafkaTopicPartition> partitions) {
        Set<TopicPartition> vertxPartitions = partitions
            .map(KafkaTopicPartition::toVertxTopicPartition)
            .collect(Collectors.toSet())
            .block();

        return Mono.fromCompletionStage(delegate.seekToEnd(vertxPartitions));
    }

    @Override
    public Mono<Long> position(KafkaTopicPartition partition) {
        return Mono.fromCompletionStage(delegate.position(partition.toVertxTopicPartition()));
    }

    @Override
    public Mono<Long> committed(KafkaTopicPartition topicPartition) {
        return Mono.fromCompletionStage(delegate.committed(topicPartition.toVertxTopicPartition()))
            .map(OffsetAndMetadata::getOffset);
    }

    @Override
    public Mono<Long> beginningOffset(KafkaTopicPartition partition) {
        return Mono.fromCompletionStage(delegate.beginningOffsets(partition.toVertxTopicPartition()));
    }

    @Override
    public Mono<Long> endOffset(KafkaTopicPartition partition) {
        return Mono.fromCompletionStage(delegate.endOffsets(partition.toVertxTopicPartition()));
    }

    @Override
    public Mono<Long> timeOffset(KafkaTopicPartition partition, long timestamp) {
        return Mono.fromCompletionStage(delegate.offsetsForTimes(partition.toVertxTopicPartition(), timestamp))
            .map(OffsetAndTimestamp::getOffset);
    }

    @Override
    public Mono<Void> commit() {
        return Mono.fromCompletionStage(delegate.commit());
    }

    @Override
    public Mono<Void> close() {
        return Mono.fromCompletionStage(delegate.close());
    }

    @Override
    @SuppressWarnings("unchecked") // Axle API returns KafkaConsumer without generics
    public <T> Mono<T> doOnVertxConsumer(Function<io.vertx.kafka.client.consumer.KafkaConsumer<K, V>, T> function) {
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
    public Mono<KafkaConsumerRecord<K, V>> mono() {
        return Mono.from(delegate.toPublisher())
            .map(SnowdropKafkaConsumerRecord::new);
    }

    @Override
    public Flux<KafkaConsumerRecord<K, V>> flux() {
        return Flux.from(delegate.toPublisher())
            .map(SnowdropKafkaConsumerRecord::new);
    }

    @Override
    public ReadStream vertxReadStream() {
        return delegate.getDelegate();
    }
}
