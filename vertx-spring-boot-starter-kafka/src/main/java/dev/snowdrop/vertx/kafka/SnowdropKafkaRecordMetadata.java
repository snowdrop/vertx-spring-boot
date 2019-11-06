package dev.snowdrop.vertx.kafka;

import java.util.Objects;

import io.vertx.kafka.client.producer.RecordMetadata;

final class SnowdropKafkaRecordMetadata implements KafkaRecordMetadata {

    private final String topic;

    private final long partition;

    private final long offset;

    private final long timestamp;

    private final long checksum;

    SnowdropKafkaRecordMetadata(RecordMetadata vertxRecordMetadata) {
        this.topic = vertxRecordMetadata.getTopic();
        this.partition = vertxRecordMetadata.getPartition();
        this.offset = vertxRecordMetadata.getOffset();
        this.timestamp = vertxRecordMetadata.getTimestamp();
        this.checksum = vertxRecordMetadata.checksum();
    }

    public String getTopic() {
        return topic;
    }

    public long getPartition() {
        return partition;
    }

    public long getOffset() {
        return offset;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getChecksum() {
        return checksum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SnowdropKafkaRecordMetadata that = (SnowdropKafkaRecordMetadata) o;

        return partition == that.partition &&
            offset == that.offset &&
            timestamp == that.timestamp &&
            checksum == that.checksum &&
            Objects.equals(topic, that.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, partition, offset, timestamp, checksum);
    }
}
