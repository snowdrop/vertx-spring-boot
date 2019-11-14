package dev.snowdrop.vertx.kafka;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.vertx.axle.kafka.client.consumer.KafkaConsumerRecord;

class SnowdropConsumerRecord<K, V> implements ConsumerRecord<K, V> {

    private final KafkaConsumerRecord<K, V> delegate;

    SnowdropConsumerRecord(KafkaConsumerRecord<K, V> delegate) {
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
    public TimestampType timestampType() {
        return new SnowdropTimestampType(delegate.timestampType());
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

        SnowdropConsumerRecord<?, ?> that = (SnowdropConsumerRecord<?, ?>) o;

        return Objects.equals(delegate, that.delegate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate);
    }
}
