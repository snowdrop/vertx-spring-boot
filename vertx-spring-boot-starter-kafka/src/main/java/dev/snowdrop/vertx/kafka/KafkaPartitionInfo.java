package dev.snowdrop.vertx.kafka;

import java.util.List;

public interface KafkaPartitionInfo {

    String getTopic();

    long getPartition();

    List<KafkaNode> getReplicas();

    List<KafkaNode> getInSyncReplicas();

    KafkaNode getLeader();
}
