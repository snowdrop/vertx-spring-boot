package dev.snowdrop.vertx.kafka;

public interface Partition {

    String topic();

    int partition();

    static Partition create(String topic, int partition) {
        return new SnowdropPartition(topic, partition);
    }
}
