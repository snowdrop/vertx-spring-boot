package dev.snowdrop.vertx.kafka;

import java.util.Objects;

import io.vertx.kafka.client.common.Node;

final class SnowdropKafkaNode implements KafkaNode {

    private int id;

    private String idString;

    private String host;

    private int port;

    private boolean hasRack;

    private String rack;

    private boolean isEmpty;

    SnowdropKafkaNode(Node vertxNode) {
        this.id = vertxNode.getId();
        this.idString = vertxNode.getIdString();
        this.host = vertxNode.getHost();
        this.port = vertxNode.getPort();
        this.hasRack = vertxNode.hasRack();
        this.rack = vertxNode.rack();
        this.isEmpty = vertxNode.isEmpty();
    }

    public int getId() {
        return id;
    }

    public String getIdString() {
        return idString;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean hasRack() {
        return hasRack;
    }

    public String getRack() {
        return rack;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SnowdropKafkaNode that = (SnowdropKafkaNode) o;

        return id == that.id &&
            port == that.port &&
            hasRack == that.hasRack &&
            isEmpty == that.isEmpty &&
            Objects.equals(idString, that.idString) &&
            Objects.equals(host, that.host) &&
            Objects.equals(rack, that.rack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idString, host, port, hasRack, rack, isEmpty);
    }

    @Override
    public String toString() {
        return String.format(
            "SnowdropKafkaNode{id=%d, idString='%s', host='%s', port=%d, hasRack=%b, rack='%s', isEmpty=%b}", id,
            idString, host, port, hasRack, rack, isEmpty);
    }
}
