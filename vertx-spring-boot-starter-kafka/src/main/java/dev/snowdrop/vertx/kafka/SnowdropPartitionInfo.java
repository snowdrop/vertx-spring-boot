package dev.snowdrop.vertx.kafka;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final class SnowdropPartitionInfo implements PartitionInfo {

    private final String topic;

    private final long partition;

    private final List<Node> replicas;

    private final List<Node> inSyncReplicas;

    private final Node leader;

    SnowdropPartitionInfo(io.vertx.kafka.client.common.PartitionInfo vertxPartitionInfo) {
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
            : new SnowdropNode(vertxPartitionInfo.getLeader()));
    }

    public String getTopic() {
        return topic;
    }

    public long getPartition() {
        return partition;
    }

    public List<Node> getReplicas() {
        return replicas;
    }

    public List<Node> getInSyncReplicas() {
        return inSyncReplicas;
    }

    public Node getLeader() {
        return leader;
    }

    private List<Node> convertNodes(List<io.vertx.kafka.client.common.Node> vertxNodes) {
        return vertxNodes.stream()
            .map(SnowdropNode::new)
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

        SnowdropPartitionInfo that = (SnowdropPartitionInfo) o;

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

    @Override
    public String toString() {
        return String.format("%s{topic='%s', partition=%d, replicas=%s, inSyncReplicas=%s, leader=%s}",
            getClass().getSimpleName(), topic, partition, replicas, inSyncReplicas, leader);
    }
}
