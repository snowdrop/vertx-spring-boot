package dev.snowdrop.vertx.kafka;

import java.util.Objects;

import io.vertx.kafka.client.consumer.OffsetAndTimestamp;

final class SnowdropKafkaOffsetAndTimestamp implements KafkaOffsetAndTimestamp {

    private final long offset;

    private final long timestamp;

    SnowdropKafkaOffsetAndTimestamp(OffsetAndTimestamp delegate) {
        this.offset = delegate.getOffset();
        this.timestamp = delegate.getTimestamp();
    }

    public long getOffset() {
        return offset;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SnowdropKafkaOffsetAndTimestamp that = (SnowdropKafkaOffsetAndTimestamp) o;

        return offset == that.offset &&
            timestamp == that.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, timestamp);
    }
}
