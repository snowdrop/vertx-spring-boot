package dev.snowdrop.vertx.kafka;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.vertx.kafka.client.common.Node;
import io.vertx.kafka.client.common.PartitionInfo;

final class SnowdropKafkaPartitionInfo implements KafkaPartitionInfo {

    private final String topic;

    private final long partition;

    private final List<KafkaNode> replicas;

    private final List<KafkaNode> inSyncReplicas;

    private final KafkaNode leader;

    SnowdropKafkaPartitionInfo(PartitionInfo vertxPartitionInfo) {
        this.topic = vertxPartitionInfo.getTopic();
        this.partition = vertxPartitionInfo.getPartition();
        this.replicas = (vertxPartitionInfo.getReplicas() == null
            ? new LinkedList<>()
            : convertNodes(vertxPartitionInfo.getReplicas()));
        this.inSyncReplicas = (vertxPartitionInfo.getInSyncReplicas() == null
            ? new LinkedList<>()
            : convertNodes(vertxPartitionInfo.getInSyncReplicas()));
        this.leader = (vertxPartitionInfo.getLeader() == null
            ? null
            : new SnowdropKafkaNode(vertxPartitionInfo.getLeader()));
    }

    public String getTopic() {
        return topic;
    }

    public long getPartition() {
        return partition;
    }

    public List<KafkaNode> getReplicas() {
        return replicas;
    }

    public List<KafkaNode> getInSyncReplicas() {
        return inSyncReplicas;
    }

    public KafkaNode getLeader() {
        return leader;
    }

    private List<KafkaNode> convertNodes(List<Node> vertxNodes) {
        return vertxNodes.stream()
            .map(SnowdropKafkaNode::new)
            .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SnowdropKafkaPartitionInfo that = (SnowdropKafkaPartitionInfo) o;
        
        return partition == that.partition &&
            Objects.equals(topic, that.topic) &&
            Objects.equals(replicas, that.replicas) &&
            Objects.equals(inSyncReplicas, that.inSyncReplicas) &&
            Objects.equals(leader, that.leader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, partition, replicas, inSyncReplicas, leader);
    }
}
