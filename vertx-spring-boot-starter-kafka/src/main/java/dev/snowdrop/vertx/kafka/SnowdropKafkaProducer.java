package dev.snowdrop.vertx.kafka;

import java.util.function.Consumer;

import io.vertx.core.streams.WriteStream;
import org.apache.kafka.clients.producer.Producer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

final class SnowdropKafkaProducer<K, V> implements KafkaProducer<K, V> {

    private final io.vertx.axle.kafka.client.producer.KafkaProducer<K, V> delegate;

    SnowdropKafkaProducer(io.vertx.axle.kafka.client.producer.KafkaProducer<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Mono<KafkaRecordMetadata> send(KafkaProducerRecord<K, V> record) {
        return Mono.fromCompletionStage(delegate.send(record.toAxleKafkaProducerRecord()))
            .map(SnowdropKafkaRecordMetadata::new);
    }

    @Override
    public Flux<KafkaPartitionInfo> partitionFor(String topic) {
        return Mono.fromCompletionStage(delegate.partitionsFor(topic))
            .flatMapMany(Flux::fromIterable)
            .map(SnowdropKafkaPartitionInfo::new);
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
    @SuppressWarnings("unchecked") // Axle uses correct generic types, but returns a delegate without them
    public Producer<K, V> unwrap() {
        return delegate.getDelegate().unwrap();
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
    public Mono<Void> write(KafkaProducerRecord<K, V> data) {
        return Mono.fromCompletionStage(delegate.write(data.toAxleKafkaProducerRecord()));
    }

    @Override
    public Mono<Void> end() {
        return Mono.fromCompletionStage(delegate.end());
    }

    @Override
    public WriteStream vertxWriteStream() {
        return delegate.getDelegate();
    }
}
