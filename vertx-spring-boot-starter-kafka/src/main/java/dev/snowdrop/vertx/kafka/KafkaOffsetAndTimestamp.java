package dev.snowdrop.vertx.kafka;

public interface KafkaOffsetAndTimestamp {

    long getOffset();

    long getTimestamp();
}
