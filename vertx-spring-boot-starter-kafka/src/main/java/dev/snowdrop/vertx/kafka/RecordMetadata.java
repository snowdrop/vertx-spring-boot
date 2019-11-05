package dev.snowdrop.vertx.kafka;

public final class RecordMetadata {

    private final String topic;

    private final long partition;

    private final long offset;

    private final long timestamp;

    private final long checksum;

    // TODO convert to builder
    public RecordMetadata(String topic, long partition, long offset, long timestamp, long checksum) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.timestamp = timestamp;
        this.checksum = checksum;
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
}
