package dev.snowdrop.vertx.kafka;

import io.vertx.kafka.client.common.Node;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SnowdropKafkaNodeTest {

    @Test
    public void shouldCreate() {
        Node vertxKafkaNode = new Node(true, "test-host", 1, "1", true, 8080, "test-rack");
        KafkaNode snowdropKafkaNode = new SnowdropKafkaNode(vertxKafkaNode);

        assertThat(snowdropKafkaNode.getId()).isEqualTo(1);
        assertThat(snowdropKafkaNode.getIdString()).isEqualTo("1");
        assertThat(snowdropKafkaNode.getHost()).isEqualTo("test-host");
        assertThat(snowdropKafkaNode.getPort()).isEqualTo(8080);
        assertThat(snowdropKafkaNode.hasRack()).isEqualTo(true);
        assertThat(snowdropKafkaNode.getRack()).isEqualTo("test-rack");
        assertThat(snowdropKafkaNode.isEmpty()).isEqualTo(true);
    }
}
