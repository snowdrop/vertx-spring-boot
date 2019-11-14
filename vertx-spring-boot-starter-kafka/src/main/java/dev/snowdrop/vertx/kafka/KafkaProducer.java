package dev.snowdrop.vertx.kafka;

import java.util.function.Consumer;
import java.util.function.Function;

import dev.snowdrop.vertx.streams.WriteStream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface KafkaProducer<K, V> extends WriteStream<ProducerRecord<K, V>> {

    Mono<KafkaRecordMetadata> send(ProducerRecord<K, V> record);

    Flux<KafkaPartitionInfo> partitionsFor(String topic);

    Mono<Void> flush();

    Mono<Void> close();

    Mono<Void> close(long timeout);

    <T> Mono<T> doOnVertxProducer(Function<io.vertx.kafka.client.producer.KafkaProducer<K, V>, T> function);

    // WriteStream methods overload

    KafkaProducer<K, V> exceptionHandler(Consumer<Throwable> handler);

    KafkaProducer<K, V> drainHandler(Consumer<Void> handler);

    KafkaProducer<K, V> setWriteQueueMaxSize(int maxSize);
}
