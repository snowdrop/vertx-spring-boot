package dev.snowdrop.vertx.kafka;

import java.util.List;

public interface ProducerRecord<K, V> {

    K key();

    V value();

    String topic();

    Integer partition();

    Long timestamp();

    List<Header> headers();

    static <K, V> ProducerRecordBuilder<? extends ProducerRecord<K, V>, K, V> builder(String topic, V value) {
        return new SnowdropProducerRecordBuilder<K, V>(topic, value);
    }

    static <K, V> ProducerRecordBuilder<? extends ProducerRecord<K, V>, K, V> builder(String topic, V value, K key) {
        return new SnowdropProducerRecordBuilder<K, V>(topic, value)
            .withKey(key);
    }
}
