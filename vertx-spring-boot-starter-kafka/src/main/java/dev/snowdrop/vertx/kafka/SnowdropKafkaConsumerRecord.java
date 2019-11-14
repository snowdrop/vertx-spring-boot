package dev.snowdrop.vertx.kafka;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class SnowdropKafkaConsumerRecord<K, V> implements KafkaConsumerRecord<K, V> {

    private final io.vertx.axle.kafka.client.consumer.KafkaConsumerRecord<K, V> delegate;

    SnowdropKafkaConsumerRecord(io.vertx.axle.kafka.client.consumer.KafkaConsumerRecord<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public String topic() {
        return delegate.topic();
    }

    @Override
    public int partition() {
        return delegate.partition();
    }

    @Override
    public long offset() {
        return delegate.offset();
    }

    @Override
    public long timestamp() {
        return delegate.timestamp();
    }

    @Override
    public KafkaTimestampType timestampType() {
        return new SnowdropKafkaTimestampType(delegate.timestampType());
    }

    @Override
    public K key() {
        return delegate.key();
    }

    @Override
    public V value() {
        return delegate.value();
    }

    @Override
    public List<Header> headers() {
        return delegate
            .headers()
            .stream()
            .map(SnowdropHeader::new)
            .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SnowdropKafkaConsumerRecord<?, ?> that = (SnowdropKafkaConsumerRecord<?, ?>) o;

        return Objects.equals(delegate, that.delegate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate);
    }
}
