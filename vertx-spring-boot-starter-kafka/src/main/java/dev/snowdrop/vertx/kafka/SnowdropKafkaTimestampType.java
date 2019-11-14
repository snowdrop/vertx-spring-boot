package dev.snowdrop.vertx.kafka;

import java.util.Objects;

import org.apache.kafka.common.record.TimestampType;

class SnowdropKafkaTimestampType implements KafkaTimestampType {

    private final TimestampType delegate;

    SnowdropKafkaTimestampType(TimestampType delegate) {
        this.delegate = delegate;
    }

    @Override
    public int getId() {
        return delegate.id;
    }

    @Override
    public String getName() {
        return delegate.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SnowdropKafkaTimestampType that = (SnowdropKafkaTimestampType) o;
        return delegate == that.delegate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate);
    }
}
