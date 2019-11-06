package dev.snowdrop.vertx.kafka;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class KafkaProducerRecord<K, V> {

    private final K key;

    private final V value;

    private final String topic;

    private final Integer partition;

    private final Long timestamp;

    private final List<KafkaHeader> headers;

    public static <K, V> Builder<K, V> builder(String topic, V value, Class<K> keyClass) {
        return new Builder<>(topic, value);
    }

    private KafkaProducerRecord(Builder<K, V> builder) {
        this.key = builder.key;
        this.value = builder.value;
        this.topic = builder.topic;
        this.partition = builder.partition;
        this.timestamp = builder.timestamp;
        this.headers = Collections.unmodifiableList(builder.headers);
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public String getTopic() {
        return topic;
    }

    public Integer getPartition() {
        return partition;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public List<KafkaHeader> getHeaders() {
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

        KafkaProducerRecord<?, ?> that = (KafkaProducerRecord<?, ?>) o;

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

    // TODO here or as a separate converter?
    io.vertx.axle.kafka.client.producer.KafkaProducerRecord<K, V> toAxleKafkaProducerRecord() {
        List<io.vertx.axle.kafka.client.producer.KafkaHeader> axleHeaders = headers.stream()
            .map(KafkaHeader::toAxleKafkaHeader)
            .collect(Collectors.toList());

        return io.vertx.axle.kafka.client.producer.KafkaProducerRecord.create(topic, key, value, timestamp, partition)
            .addHeaders(axleHeaders);
    }

    public static class Builder<K, V> {

        private final String topic;

        private final V value;

        private final List<KafkaHeader> headers;

        private K key;

        private Integer partition;

        private Long timestamp;

        private Builder(String topic, V value) {
            this.topic = topic;
            this.value = value;
            this.headers = new LinkedList<>();
        }

        public Builder<K, V> withKey(K key) {
            this.key = key;
            return this;
        }

        public Builder<K, V> withPartition(int partition) {
            if (partition < 0) {
                throw new IllegalArgumentException(
                    String.format("Invalid partition: %d. Partition number cannot be negative.", partition));
            }
            this.partition = partition;
            return this;
        }

        public Builder<K, V> withTimestamp(long timestamp) {
            if (timestamp < 0) {
                throw new IllegalArgumentException(
                    String.format("Invalid timestamp: %d. Timestamp cannot be negative.", timestamp));
            }
            this.timestamp = timestamp;
            return this;
        }

        public Builder<K, V> withHeader(KafkaHeader header) {
            headers.add(header);
            return this;
        }

        public Builder<K, V> withHeaders(List<KafkaHeader> headers) {
            this.headers.addAll(headers);
            return this;
        }

        public KafkaProducerRecord<K, V> build() {
            return new KafkaProducerRecord<>(this);
        }
    }
}
