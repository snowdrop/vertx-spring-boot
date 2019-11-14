package dev.snowdrop.vertx.kafka;

import java.util.Objects;

import io.vertx.kafka.client.consumer.OffsetAndMetadata;

class SnowdropKafkaOffsetAndMetadata implements KafkaOffsetAndMetadata {

    private final long offset;

    private final String metadata;

    SnowdropKafkaOffsetAndMetadata(long offset, String metadata) {
        this.offset = offset;
        this.metadata = metadata;
    }

    SnowdropKafkaOffsetAndMetadata(OffsetAndMetadata vertxOffsetAndMetadata) {
        this.offset = vertxOffsetAndMetadata.getOffset();
        this.metadata = vertxOffsetAndMetadata.getMetadata();
    }

    public long getOffset() {
        return offset;
    }

    public String getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return String.format("SnowdropKafkaOffsetAndMetadata{offset=%d, metadata='%s'}", offset, metadata);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SnowdropKafkaOffsetAndMetadata that = (SnowdropKafkaOffsetAndMetadata) o;
        return offset == that.offset &&
            Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, metadata);
    }
}
