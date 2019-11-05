package dev.snowdrop.vertx.kafka;

public final class OffsetAndTimestamp {

    private final long offset;

    private final long timestamp;

    // TODO replace with a builder
    public OffsetAndTimestamp(long offset, long timestamp) {
        this.offset = offset;
        this.timestamp = timestamp;
    }

    public long getOffset() {
        return offset;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
