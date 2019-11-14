package dev.snowdrop.vertx.kafka;

public interface KafkaOffsetAndMetadata {

    long getOffset();

    String getMetadata();
}
