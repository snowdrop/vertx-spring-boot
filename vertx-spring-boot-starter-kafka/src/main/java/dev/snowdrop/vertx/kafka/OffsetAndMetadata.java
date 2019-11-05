package dev.snowdrop.vertx.kafka;

public final class OffsetAndMetadata {

    private final long offset;

    private final String metadata;

    public OffsetAndMetadata(long offset, String metadata) {
        this.offset = offset;
        this.metadata = metadata;
    }

    public long getOffset() {
        return offset;
    }

    public String getMetadata() {
        return metadata;
    }
}
