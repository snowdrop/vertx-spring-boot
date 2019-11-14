package dev.snowdrop.vertx.kafka;

import java.util.List;

public interface KafkaConsumerRecord<K, V> {

    String topic();

    int partition();

    long offset();

    long timestamp();

    KafkaTimestampType timestampType();

    K key();

    V value();

    List<KafkaHeader> headers();
}
