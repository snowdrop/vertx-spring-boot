package dev.snowdrop.vertx.kafka;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SnowdropNodeTest {

    @Test
    public void shouldCreate() {
        io.vertx.kafka.client.common.Node
            vertxNode = new io.vertx.kafka.client.common.Node(true, "test-host", 1, "1", true, 8080, "test-rack");
        Node snowdropNode = new SnowdropNode(vertxNode);

        assertThat(snowdropNode.getId()).isEqualTo(1);
        assertThat(snowdropNode.getIdString()).isEqualTo("1");
        assertThat(snowdropNode.getHost()).isEqualTo("test-host");
        assertThat(snowdropNode.getPort()).isEqualTo(8080);
        assertThat(snowdropNode.hasRack()).isEqualTo(true);
        assertThat(snowdropNode.getRack()).isEqualTo("test-rack");
        assertThat(snowdropNode.isEmpty()).isEqualTo(true);
    }
}
