package dev.snowdrop.vertx.kafka;

public final class TopicPartition {

    private final String topic;

    private final int partition;

    public TopicPartition(String topic, int partition) {
        this.topic = topic;
        this.partition = partition;
    }

    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }
}
