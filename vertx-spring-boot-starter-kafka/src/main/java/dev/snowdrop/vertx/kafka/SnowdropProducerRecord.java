package dev.snowdrop.vertx.kafka;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class SnowdropProducerRecord<K, V> implements ProducerRecord<K, V> {

    private final K key;

    private final V value;

    private final String topic;

    private final Integer partition;

    private final Long timestamp;

    private final List<Header> headers;

    SnowdropProducerRecord(K key, V value, String topic, Integer partition, Long timestamp, List<Header> headers) {
        this.key = key;
        this.value = value;
        this.topic = topic;
        this.partition = partition;
        this.timestamp = timestamp;
        this.headers = Collections.unmodifiableList(headers);
    }

    @Override
    public K key() {
        return key;
    }

    @Override
    public V value() {
        return value;
    }

    @Override
    public String topic() {
        return topic;
    }

    @Override
    public Integer partition() {
        return partition;
    }

    @Override
    public Long timestamp() {
        return timestamp;
    }

    @Override
    public List<Header> headers() {
        return headers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SnowdropProducerRecord<?, ?> that = (SnowdropProducerRecord<?, ?>) o;

        return Objects.equals(key, that.key) &&
            Objects.equals(value, that.value) &&
            Objects.equals(topic, that.topic) &&
            Objects.equals(partition, that.partition) &&
            Objects.equals(timestamp, that.timestamp) &&
            Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value, topic, partition, timestamp, headers);
    }
}
