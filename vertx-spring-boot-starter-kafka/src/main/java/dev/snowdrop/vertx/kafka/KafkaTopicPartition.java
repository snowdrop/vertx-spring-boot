package dev.snowdrop.vertx.kafka;

import java.util.Objects;

import io.vertx.kafka.client.common.TopicPartition;
import org.springframework.util.StringUtils;

public final class KafkaTopicPartition {

    private final String topic;

    private final int partition;

    public static KafkaTopicPartition create(String topic, int partition) {
        if (StringUtils.isEmpty(topic)) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }

        if (partition < 0) {
            throw new IllegalArgumentException("Partition cannot be negative");
        }

        return new KafkaTopicPartition(topic, partition);
    }

    static KafkaTopicPartition create(TopicPartition vertxTopicPartition) {
        return new KafkaTopicPartition(vertxTopicPartition.getTopic(), vertxTopicPartition.getPartition());
    }

    private KafkaTopicPartition(String topic, int partition) {
        this.topic = topic;
        this.partition = partition;
    }

    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KafkaTopicPartition that = (KafkaTopicPartition) o;

        return partition == that.partition &&
            Objects.equals(topic, that.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, partition);
    }

    @Override
    public String toString() {
        return String.format("%s{topic='%s', partition=%d}", KafkaTopicPartition.class.getSimpleName(), topic,
            partition);
    }

    // TODO here or as a separate converter?
    io.vertx.kafka.client.common.TopicPartition toVertxTopicPartition() {
        return new io.vertx.kafka.client.common.TopicPartition(topic, partition);
    }
}
