package dev.snowdrop.vertx.kafka;

import java.util.function.Consumer;

import dev.snowdrop.vertx.streams.WriteStream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface KafkaProducer<K, V> extends WriteStream<KafkaProducerRecord<K, V>> {

    Mono<RecordMetadata> send(KafkaProducerRecord<K, V> record);

    Flux<PartitionInfo> partitionFor(String topic);

    Mono<Void> flush();

    Mono<Void> close();

    Mono<Void> close(long timeout);

    org.apache.kafka.clients.producer.Producer<K, V> unwrap();

    // WriteStream methods overload

    KafkaProducer<K, V> exceptionHandler(Consumer<Throwable> handler);

    KafkaProducer<K, V> drainHandler(Consumer<Void> handler);

    KafkaProducer<K, V> setWriteQueueMaxSize(int maxSize);
}
