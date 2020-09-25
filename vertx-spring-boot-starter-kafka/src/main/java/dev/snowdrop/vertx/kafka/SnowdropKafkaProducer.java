package dev.snowdrop.vertx.kafka;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.smallrye.mutiny.converters.uni.UniReactorConverters;
import io.vertx.core.streams.WriteStream;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.kafka.client.producer.KafkaHeader;
import io.vertx.mutiny.kafka.client.producer.KafkaProducerRecord;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

final class SnowdropKafkaProducer<K, V> implements KafkaProducer<K, V> {

    private final io.vertx.mutiny.kafka.client.producer.KafkaProducer<K, V> delegate;

    SnowdropKafkaProducer(io.vertx.mutiny.kafka.client.producer.KafkaProducer<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Mono<RecordMetadata> send(ProducerRecord<K, V> record) {
        Objects.requireNonNull(record, "Record cannot be null");

        return delegate.send(toMutinyProducerRecord(record))
            .convert()
            .with(UniReactorConverters.toMono())
            .map(SnowdropRecordMetadata::new);
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
        return delegate.close().convert().with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Void> close(long timeout) {
        return delegate.close(timeout).convert().with(UniReactorConverters.toMono());
    }

    @Override
    @SuppressWarnings("unchecked") // Mutiny API returns KafkaProducer without generics
    public <T> Mono<T> doOnVertxProducer(Function<io.vertx.kafka.client.producer.KafkaProducer<K, V>, T> function) {
        Objects.requireNonNull(function, "Function cannot be null");

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
        Objects.requireNonNull(record, "Record cannot be null");

        return delegate.write(toMutinyProducerRecord(record)).convert().with(UniReactorConverters.toMono());
    }

    @Override
    public Mono<Void> end() {
        return delegate.end().convert().with(UniReactorConverters.toMono());
    }

    @Override
    public WriteStream vertxWriteStream() {
        return delegate.getDelegate();
    }

    private KafkaProducerRecord<K, V> toMutinyProducerRecord(ProducerRecord<K, V> record) {
        List<KafkaHeader> mutinyHeaders = record
            .headers()
            .stream()
            .map(this::toMutinyHeader)
            .collect(Collectors.toList());

        return KafkaProducerRecord
            .create(record.topic(), record.key(), record.value(), record.timestamp(), record.partition())
            .addHeaders(mutinyHeaders);
    }

    private KafkaHeader toMutinyHeader(Header header) {
        return KafkaHeader.header(header.key(), Buffer.buffer(header.value().asByteBuffer().array()));
    }
}
