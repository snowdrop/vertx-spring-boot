package dev.snowdrop.vertx.kafka;

import java.util.List;

public final class PartitionInfo {

    private final String topic;

    private final long partition;

    private final List<Node> replicas;

    private final List<Node> inSyncReplicas;

    private final Node leader;

    // TODO replace with a builder
    public PartitionInfo(String topic, long partition, List<Node> replicas, List<Node> inSyncReplicas, Node leader) {
        this.topic = topic;
        this.partition = partition;
        this.replicas = replicas;
        this.inSyncReplicas = inSyncReplicas;
        this.leader = leader;
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
}
