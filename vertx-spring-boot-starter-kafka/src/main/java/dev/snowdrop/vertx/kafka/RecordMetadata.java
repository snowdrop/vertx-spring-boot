package dev.snowdrop.vertx.kafka;

public interface RecordMetadata {

    String topic();

    long partition();

    long offset();

    long timestamp();

    long checksum();
}
