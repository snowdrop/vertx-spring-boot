package dev.snowdrop.vertx.kafka;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.vertx.axle.core.buffer.Buffer;
import io.vertx.axle.kafka.client.producer.KafkaHeader;
import io.vertx.axle.kafka.client.producer.KafkaProducerRecord;
import io.vertx.core.streams.WriteStream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// TODO make package-private once Spring configuration is setup
public final class SnowdropKafkaProducer<K, V> implements KafkaProducer<K, V> {

    private final io.vertx.axle.kafka.client.producer.KafkaProducer<K, V> delegate;

    // TODO make package-private once Spring configuration is setup
    public SnowdropKafkaProducer(io.vertx.axle.kafka.client.producer.KafkaProducer<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Mono<RecordMetadata> send(ProducerRecord<K, V> record) {
        return Mono.fromCompletionStage(delegate.send(toAxleProducerRecord(record)))
            .map(SnowdropRecordMetadata::new);
    }

    @Override
    public Flux<PartitionInfo> partitionsFor(String topic) {
        return Mono.fromCompletionStage(delegate.partitionsFor(topic))
            .flatMapMany(Flux::fromIterable)
            .map(SnowdropPartitionInfo::new);
    }

    @Override
    public Mono<Void> flush() {
        return Mono.create(sink -> {
            try {
                delegate.flush(v -> sink.success());
            } catch (Throwable t) {
                sink.error(t);
            }
        });
    }

    @Override
    public Mono<Void> close() {
        return Mono.fromCompletionStage(delegate.close());
    }

    @Override
    public Mono<Void> close(long timeout) {
        return Mono.fromCompletionStage(delegate.close(timeout));
    }

    @Override
    @SuppressWarnings("unchecked") // Axle API returns KafkaProducer without generics
    public <T> Mono<T> doOnVertxProducer(Function<io.vertx.kafka.client.producer.KafkaProducer<K, V>, T> function) {
        return Mono.create(sink -> {
            try {
                T result = function.apply((io.vertx.kafka.client.producer.KafkaProducer<K, V>) delegate.getDelegate());
                sink.success(result);
            } catch (Throwable t) {
                sink.error(t);
            }
        });
    }

    @Override
    public KafkaProducer<K, V> exceptionHandler(Consumer<Throwable> handler) {
        delegate.exceptionHandler(handler);
        return this;
    }

    @Override
    public KafkaProducer<K, V> drainHandler(Consumer<Void> handler) {
        delegate.drainHandler(handler);
        return this;
    }

    @Override
    public KafkaProducer<K, V> setWriteQueueMaxSize(int maxSize) {
        delegate.setWriteQueueMaxSize(maxSize);
        return this;
    }

    @Override
    public boolean writeQueueFull() {
        return delegate.writeQueueFull();
    }

    @Override
    public Mono<Void> write(ProducerRecord<K, V> record) {
        return Mono.fromCompletionStage(delegate.write(toAxleProducerRecord(record)));
    }

    @Override
    public Mono<Void> end() {
        return Mono.fromCompletionStage(delegate.end());
    }

    @Override
    public WriteStream vertxWriteStream() {
        return delegate.getDelegate();
    }

    private KafkaProducerRecord<K, V> toAxleProducerRecord(ProducerRecord<K, V> record) {
        List<KafkaHeader> axleHeaders = record
            .headers()
            .stream()
            .map(this::toAxleHeader)
            .collect(Collectors.toList());

        return KafkaProducerRecord
            .create(record.topic(), record.key(), record.value(), record.timestamp(), record.partition())
            .addHeaders(axleHeaders);
    }

    private KafkaHeader toAxleHeader(Header header) {
        return KafkaHeader.header(header.key(), Buffer.buffer(header.value().asByteBuffer().array()));
    }
}
