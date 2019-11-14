package dev.snowdrop.vertx.kafka;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SnowdropPartitionInfoTest {

    @Test
    public void shouldCreate() {
        List<io.vertx.kafka.client.common.Node> vertxNodes = Arrays.asList(
            new io.vertx.kafka.client.common.Node(true, "test-host", 1, "1", true, 8080, "test-rack"),
            new io.vertx.kafka.client.common.Node(true, "test-host", 2, "2", true, 8080, "test-rack")
        );
        io.vertx.kafka.client.common.PartitionInfo vertxPartitionInfo =
            new io.vertx.kafka.client.common.PartitionInfo(vertxNodes, vertxNodes.get(0), 1, vertxNodes, "test-topic");
        List<Node> snowdropNodes =
            Arrays.asList(new SnowdropNode(vertxNodes.get(0)), new SnowdropNode(vertxNodes.get(1)));

        PartitionInfo snowdropPartitionInfo = new SnowdropPartitionInfo(vertxPartitionInfo);
        assertThat(snowdropPartitionInfo.getTopic()).isEqualTo("test-topic");
        assertThat(snowdropPartitionInfo.getPartition()).isEqualTo(1);
        assertThat(snowdropPartitionInfo.getReplicas()).containsAll(snowdropNodes);
        assertThat(snowdropPartitionInfo.getInSyncReplicas()).containsAll(snowdropNodes);
        assertThat(snowdropPartitionInfo.getLeader()).isEqualTo(snowdropNodes.get(0));
    }
}
