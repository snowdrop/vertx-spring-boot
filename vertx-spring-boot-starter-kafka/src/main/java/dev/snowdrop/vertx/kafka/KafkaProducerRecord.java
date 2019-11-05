package dev.snowdrop.vertx.kafka;

import java.util.List;

import org.springframework.core.io.buffer.DataBuffer;

public interface KafkaProducerRecord<K, V> {

    String topic();

    K key();

    V value();

    long timestamp();

    int partition();

    KafkaProducerRecord<K, V> addHeader(String key, String value);

    KafkaProducerRecord<K, V> addHeader(String key, DataBuffer value);

    KafkaProducerRecord<K, V> addHeader(KafkaHeader header);

    KafkaProducerRecord<K, V> addHeaders(List<KafkaHeader> header);

    List<KafkaHeader> headers();

    org.apache.kafka.clients.producer.ProducerRecord<K, V> unwrap();
}
