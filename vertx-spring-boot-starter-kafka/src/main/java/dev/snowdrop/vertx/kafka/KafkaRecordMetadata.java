package dev.snowdrop.vertx.kafka;

public interface KafkaRecordMetadata {

    String getTopic();

    long getPartition();

    long getOffset();

    long getTimestamp();

    long getChecksum();
}
