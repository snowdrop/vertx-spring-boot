package dev.snowdrop.vertx.kafka;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.vertx.mutiny.kafka.client.consumer.KafkaConsumerRecord;

final class SnowdropConsumerRecord<K, V> implements ConsumerRecord<K, V> {

    private final String topic;

    private final int partition;

    private final long offset;

    private final long timestamp;

    private final TimestampType timestampType;

    private final K key;

    private final V value;

    private final List<Header> headers;

    SnowdropConsumerRecord(KafkaConsumerRecord<K, V> delegate) {
        this.topic = delegate.topic();
        this.partition = delegate.partition();
        this.offset = delegate.offset();
        this.timestamp = delegate.timestamp();
        this.timestampType =
            delegate.timestampType() == null ? null : new SnowdropTimestampType(delegate.timestampType());
        this.key = delegate.key();
        this.value = delegate.value();
        this.headers =
            delegate.headers() == null ? new LinkedList<>() : delegate
                .headers()
                .stream()
                .map(SnowdropHeader::new)
                .collect(Collectors.toList());
    }

    @Override
    public String topic() {
        return topic;
    }

    @Override
    public int partition() {
        return partition;
    }

    @Override
    public long offset() {
        return offset;
    }

    @Override
    public long timestamp() {
        return timestamp;
    }

    @Override
    public TimestampType timestampType() {
        return timestampType;
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
    public List<Header> headers() {
        return new LinkedList<>(headers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SnowdropConsumerRecord<?, ?> that = (SnowdropConsumerRecord<?, ?>) o;
        return partition == that.partition &&
            offset == that.offset &&
            timestamp == that.timestamp &&
            Objects.equals(topic, that.topic) &&
            Objects.equals(timestampType, that.timestampType) &&
            Objects.equals(key, that.key) &&
            Objects.equals(value, that.value) &&
            Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, partition, offset, timestamp, timestampType, key, value, headers);
    }

    @Override
    public String toString() {
        return String.format(
            "%s{topic='%s', partition=%d, offset=%d, timestamp=%d, timestampType='%s', key='%s', value='%s', headers='%s'}",
            getClass().getSimpleName(), topic, partition, offset, timestamp, timestampType, key, value, headers);
    }
}
