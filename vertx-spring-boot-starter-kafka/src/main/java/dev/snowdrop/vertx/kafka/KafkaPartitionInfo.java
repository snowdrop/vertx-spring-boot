package dev.snowdrop.vertx.kafka;

import java.util.List;

public interface KafkaPartitionInfo {

    String getTopic();

    long getPartition();

    List<Node> getReplicas();

    List<Node> getInSyncReplicas();

    Node getLeader();
}
