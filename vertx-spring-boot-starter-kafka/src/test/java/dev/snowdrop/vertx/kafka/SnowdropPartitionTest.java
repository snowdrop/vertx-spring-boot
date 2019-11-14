package dev.snowdrop.vertx.kafka;

import io.vertx.kafka.client.common.TopicPartition;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SnowdropPartitionTest {

    @Test
    public void shouldCreateFromValues() {
        SnowdropPartition partition = new SnowdropPartition("test-topic", 1);

        assertThat(partition.topic()).isEqualTo("test-topic");
        assertThat(partition.partition()).isEqualTo(1);
    }

    @Test
    public void shouldCreateFromVertxTopicPartition() {
        TopicPartition vertxPartition = new TopicPartition("test-topic", 1);
        SnowdropPartition partition = new SnowdropPartition(vertxPartition);

        assertThat(partition.topic()).isEqualTo("test-topic");
        assertThat(partition.partition()).isEqualTo(1);
    }

    @Test
    public void shouldNotAllowEmptyTopic() {
        try {
            new SnowdropPartition(null, 1);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Topic cannot be empty");
        }
    }

    @Test
    public void shouldNotAllowNegativePartition() {
        try {
            new SnowdropPartition("test-topic", -1);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Partition cannot be negative");
        }
    }
}
