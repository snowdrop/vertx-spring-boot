package dev.snowdrop.vertx.kafka;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.vertx.core.streams.ReadStream;
import io.vertx.kafka.client.common.TopicPartition;
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
    public Mono<Void> subscribe(Set<String> topics) {
        return Mono.fromCompletionStage(delegate.subscribe(topics));
    }

    @Override
    public Mono<Void> assign(KafkaTopicPartition partition) {
        return Mono.fromCompletionStage(delegate.assign(partition.toVertxTopicPartition()));
    }

    @Override
    public Mono<Void> assign(Set<KafkaTopicPartition> partitions) {
        Set<TopicPartition> vertxTopicPartitions = partitions.stream()
            .map(KafkaTopicPartition::toVertxTopicPartition)
            .collect(Collectors.toSet());

        return Mono.fromCompletionStage(delegate.assign(vertxTopicPartitions));
    }

    @Override
    public Flux<KafkaTopicPartition> assignment() {
        return Mono.fromCompletionStage(delegate.assignment())
            .flatMapMany(Flux::fromIterable)
            .map(KafkaTopicPartition::create);
    }

    @Override
    public Mono<Void> unsubscribe() {
        return Mono.fromCompletionStage(delegate.unsubscribe());
    }

    @Override
    public Flux<String> subscription() {
        return Mono.fromCompletionStage(delegate.subscription())
            .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Void> pause(KafkaTopicPartition partition) {
        return Mono.fromCompletionStage(delegate.pause(partition.toVertxTopicPartition()));
    }

    @Override
    public Mono<Void> pause(Set<KafkaTopicPartition> partitions) {
        Set<TopicPartition> vertxTopicPartitions = partitions.stream()
            .map(KafkaTopicPartition::toVertxTopicPartition)
            .collect(Collectors.toSet());

        return Mono.fromCompletionStage(delegate.pause(vertxTopicPartitions));
    }

    @Override
    public Flux<KafkaTopicPartition> paused() {
        return Mono.fromCompletionStage(delegate.paused())
            .flatMapMany(Flux::fromIterable)
            .map(KafkaTopicPartition::create);
    }

    @Override
    public Mono<Void> resume(KafkaTopicPartition partition) {
        return Mono.fromCompletionStage(delegate.resume(partition.toVertxTopicPartition()));
    }

    @Override
    public Mono<Void> resume(Set<KafkaTopicPartition> partitions) {
        Set<TopicPartition> vertxTopicPartitions = partitions.stream()
            .map(KafkaTopicPartition::toVertxTopicPartition)
            .collect(Collectors.toSet());

        return Mono.fromCompletionStage(delegate.resume(vertxTopicPartitions));
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
    public Mono<Void> seekToBeginning(Set<KafkaTopicPartition> partitions) {
        Set<TopicPartition> vertxTopicPartitions = partitions
            .stream()
            .map(KafkaTopicPartition::toVertxTopicPartition)
            .collect(Collectors.toSet());

        return Mono.fromCompletionStage(delegate.seekToBeginning(vertxTopicPartitions));
    }

    @Override
    public Mono<Void> seekToEnd(KafkaTopicPartition partition) {
        return Mono.fromCompletionStage(delegate.seekToEnd(partition.toVertxTopicPartition()));
    }

    @Override
    public Mono<Void> seekToEnd(Set<KafkaTopicPartition> partitions) {
        Set<TopicPartition> vertxTopicPartitions = partitions
            .stream()
            .map(KafkaTopicPartition::toVertxTopicPartition)
            .collect(Collectors.toSet());

        return Mono.fromCompletionStage(delegate.seekToEnd(vertxTopicPartitions));
    }

    @Override
    public Mono<Void> commit() {
        return Mono.fromCompletionStage(delegate.commit());
    }

    @Override
    public Mono<KafkaOffsetAndMetadata> committed(KafkaTopicPartition topicPartition) {
        return Mono.fromCompletionStage(delegate.committed(topicPartition.toVertxTopicPartition()))
            .map(SnowdropKafkaOffsetAndMetadata::new);
    }

    @Override
    public Flux<KafkaPartitionInfo> partitionsFor(String topic) {
        return Mono.fromCompletionStage(delegate.partitionsFor(topic))
            .flatMapMany(Flux::fromIterable)
            .map(SnowdropKafkaPartitionInfo::new);
    }

    @Override
    public void batchHandler(Consumer<KafkaConsumerRecords<K, V>> handler) {
        delegate.batchHandler(axleRecords -> handler.accept(new SnowdropKafkaConsumerRecords<>(axleRecords)));
    }

    @Override
    public Mono<Void> close() {
        return Mono.fromCompletionStage(delegate.close());
    }

    @Override
    public Mono<Long> position(KafkaTopicPartition partition) {
        return Mono.fromCompletionStage(delegate.position(partition.toVertxTopicPartition()));
    }

    @Override
    public Mono<KafkaOffsetAndTimestamp> offsetsForTimes(KafkaTopicPartition partition, Long timestamp) {
        return Mono.fromCompletionStage(delegate.offsetsForTimes(partition.toVertxTopicPartition(), timestamp))
            .map(SnowdropKafkaOffsetAndTimestamp::new);
    }

    @Override
    public Mono<Long> beginningOffsets(KafkaTopicPartition partition) {
        return Mono.fromCompletionStage(delegate.beginningOffsets(partition.toVertxTopicPartition()));
    }

    @Override
    public Mono<Long> endOffsets(KafkaTopicPartition partition) {
        return Mono.fromCompletionStage(delegate.endOffsets(partition.toVertxTopicPartition()));
    }

    @Override
    public void pollTimeout(long timeout) {
        delegate.pollTimeout(timeout);
    }

    @Override
    public Mono<KafkaConsumerRecords<K, V>> poll(long timeout) {
        return Mono.fromCompletionStage(delegate.poll(timeout))
            .map(SnowdropKafkaConsumerRecords::new);
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
