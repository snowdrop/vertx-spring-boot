package dev.snowdrop.vertx.kafka;

import java.util.Objects;

final class SnowdropRecordMetadata implements RecordMetadata {

    private final String topic;

    private final long partition;

    private final long offset;

    private final long timestamp;

    SnowdropRecordMetadata(io.vertx.kafka.client.producer.RecordMetadata vertxRecordMetadata) {
        this.topic = vertxRecordMetadata.getTopic();
        this.partition = vertxRecordMetadata.getPartition();
        this.offset = vertxRecordMetadata.getOffset();
        this.timestamp = vertxRecordMetadata.getTimestamp();
    }

    public String topic() {
        return topic;
    }

    public long partition() {
        return partition;
    }

    public long offset() {
        return offset;
    }

    public long timestamp() {
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

        SnowdropRecordMetadata that = (SnowdropRecordMetadata) o;

        return partition == that.partition &&
            offset == that.offset &&
            timestamp == that.timestamp &&
            Objects.equals(topic, that.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, partition, offset, timestamp);
    }

    @Override
    public String toString() {
        return String.format("%s{topic='%s', partition=%d, offset=%d, timestamp=%d}",
            getClass().getSimpleName(), topic, partition, offset, timestamp);
    }
}
