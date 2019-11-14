package dev.snowdrop.vertx.kafka;

import java.util.Arrays;
import java.util.List;

import io.vertx.kafka.client.common.PartitionInfo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SnowdropKafkaPartitionInfoTest {

    @Test
    public void shouldCreate() {
        List<io.vertx.kafka.client.common.Node> vertxNodes = Arrays.asList(
            new io.vertx.kafka.client.common.Node(true, "test-host", 1, "1", true, 8080, "test-rack"),
            new io.vertx.kafka.client.common.Node(true, "test-host", 2, "2", true, 8080, "test-rack")
        );
        PartitionInfo vertxKafkaPartitionInfo =
            new PartitionInfo(vertxNodes, vertxNodes.get(0), 1, vertxNodes, "test-topic");
        List<Node> snowdropNodes =
            Arrays.asList(new SnowdropNode(vertxNodes.get(0)), new SnowdropNode(vertxNodes.get(1)));

        KafkaPartitionInfo snowdropKafkaPartitionInfo = new SnowdropKafkaPartitionInfo(vertxKafkaPartitionInfo);
        assertThat(snowdropKafkaPartitionInfo.getTopic()).isEqualTo("test-topic");
        assertThat(snowdropKafkaPartitionInfo.getPartition()).isEqualTo(1);
        assertThat(snowdropKafkaPartitionInfo.getReplicas()).containsAll(snowdropNodes);
        assertThat(snowdropKafkaPartitionInfo.getInSyncReplicas()).containsAll(snowdropNodes);
        assertThat(snowdropKafkaPartitionInfo.getLeader()).isEqualTo(snowdropNodes.get(0));
    }
}
