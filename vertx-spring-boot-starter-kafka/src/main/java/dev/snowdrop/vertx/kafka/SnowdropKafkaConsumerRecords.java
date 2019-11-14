package dev.snowdrop.vertx.kafka;

import java.util.Objects;

class SnowdropKafkaConsumerRecords<K, V> implements KafkaConsumerRecords<K, V> {

    private final io.vertx.axle.kafka.client.consumer.KafkaConsumerRecords<K, V> delegate;

    SnowdropKafkaConsumerRecords(io.vertx.axle.kafka.client.consumer.KafkaConsumerRecords<K, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public KafkaConsumerRecord<K, V> recordAt(int index) {
        return new SnowdropKafkaConsumerRecord<>(delegate.recordAt(index));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SnowdropKafkaConsumerRecords<?, ?> that = (SnowdropKafkaConsumerRecords<?, ?>) o;

        return Objects.equals(delegate, that.delegate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate);
    }
}
