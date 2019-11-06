package dev.snowdrop.vertx.kafka;

public interface KafkaNode {

    int getId();

    String getIdString();

    String getHost();

    int getPort();

    boolean hasRack();

    String getRack();

    boolean isEmpty();
}
