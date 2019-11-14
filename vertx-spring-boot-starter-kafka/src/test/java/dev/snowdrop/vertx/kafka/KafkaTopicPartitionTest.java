package dev.snowdrop.vertx.kafka;

import io.vertx.kafka.client.common.TopicPartition;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KafkaTopicPartitionTest {

    @Test
    public void shouldCreateFromValues() {
        KafkaTopicPartition partition = KafkaTopicPartition.create("test-topic", 1);

        assertThat(partition.getTopic()).isEqualTo("test-topic");
        assertThat(partition.getPartition()).isEqualTo(1);
    }

    @Test
    public void shouldCreateFromVertxTopicPartition() {
        TopicPartition vertxPartition = new TopicPartition("test-topic", 1);
        KafkaTopicPartition partition = KafkaTopicPartition.create(vertxPartition);

        assertThat(partition.getTopic()).isEqualTo("test-topic");
        assertThat(partition.getPartition()).isEqualTo(1);
    }

    @Test
    public void shouldNotAllowEmptyTopic() {
        try {
            KafkaTopicPartition.create(null, 1);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Topic cannot be empty");
        }
    }

    @Test
    public void shouldNotAllowNegativePartition() {
        try {
            KafkaTopicPartition.create("test-topic", -1);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Partition cannot be negative");
        }
    }

    @Test
    public void shouldConvertToVertxTopicPartition() {
        io.vertx.kafka.client.common.TopicPartition vertxPartition = KafkaTopicPartition
            .create("test-topic", 1)
            .toVertxTopicPartition();

        assertThat(vertxPartition.getTopic()).isEqualTo("test-topic");
        assertThat(vertxPartition.getPartition()).isEqualTo(1);
    }
}
