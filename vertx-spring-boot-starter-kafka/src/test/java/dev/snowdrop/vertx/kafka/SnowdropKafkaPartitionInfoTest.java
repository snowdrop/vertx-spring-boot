package dev.snowdrop.vertx.kafka;

import java.util.Arrays;
import java.util.List;

import io.vertx.kafka.client.common.Node;
import io.vertx.kafka.client.common.PartitionInfo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SnowdropKafkaPartitionInfoTest {

    @Test
    public void shouldCreate() {
        List<Node> vertxKafkaNodes = Arrays.asList(
            new Node(true, "test-host", 1, "1", true, 8080, "test-rack"),
            new Node(true, "test-host", 2, "2", true, 8080, "test-rack")
        );
        PartitionInfo vertxKafkaPartitionInfo =
            new PartitionInfo(vertxKafkaNodes, vertxKafkaNodes.get(0), 1, vertxKafkaNodes, "test-topic");
        List<KafkaNode> snowdropKafkaNodes =
            Arrays.asList(new SnowdropKafkaNode(vertxKafkaNodes.get(0)), new SnowdropKafkaNode(vertxKafkaNodes.get(1)));

        KafkaPartitionInfo snowdropKafkaPartitionInfo = new SnowdropKafkaPartitionInfo(vertxKafkaPartitionInfo);
        assertThat(snowdropKafkaPartitionInfo.getTopic()).isEqualTo("test-topic");
        assertThat(snowdropKafkaPartitionInfo.getPartition()).isEqualTo(1);
        assertThat(snowdropKafkaPartitionInfo.getReplicas()).containsAll(snowdropKafkaNodes);
        assertThat(snowdropKafkaPartitionInfo.getInSyncReplicas()).containsAll(snowdropKafkaNodes);
        assertThat(snowdropKafkaPartitionInfo.getLeader()).isEqualTo(snowdropKafkaNodes.get(0));
    }
}
