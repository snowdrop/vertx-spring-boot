package dev.snowdrop.vertx.kafka;

import java.util.Objects;

import io.vertx.kafka.client.common.TopicPartition;
import org.springframework.util.StringUtils;

final class SnowdropPartition implements Partition {

    private final String topic;

    private final int partition;

    SnowdropPartition(String topic, int partition) {
        if (StringUtils.isEmpty(topic)) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }

        if (partition < 0) {
            throw new IllegalArgumentException("Partition cannot be negative");
        }

        this.topic = topic;
        this.partition = partition;
    }

    SnowdropPartition(TopicPartition topicPartition) {
        this(topicPartition.getTopic(), topicPartition.getPartition());
    }

    @Override
    public String topic() {
        return topic;
    }

    @Override
    public int partition() {
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

        SnowdropPartition that = (SnowdropPartition) o;

        return partition == that.partition &&
            Objects.equals(topic, that.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, partition);
    }

    @Override
    public String toString() {
        return String.format("%s{topic='%s', partition=%d}", SnowdropPartition.class.getSimpleName(), topic,
            partition);
    }
}
