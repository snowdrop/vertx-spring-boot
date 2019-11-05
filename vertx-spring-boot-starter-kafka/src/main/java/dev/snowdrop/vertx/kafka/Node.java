package dev.snowdrop.vertx.kafka;

public final class Node {

    private int id;

    private String idString;

    private String host;

    private int port;

    private boolean hasRack;

    private String rack;

    private boolean isEmpty;

    // TODO replace with a builder
    public Node(int id, String idString, String host, int port, boolean hasRack, String rack, boolean isEmpty) {
        this.id = id;
        this.idString = idString;
        this.host = host;
        this.port = port;
        this.hasRack = hasRack;
        this.rack = rack;
        this.isEmpty = isEmpty;
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

    public boolean isHasRack() {
        return hasRack;
    }

    public String getRack() {
        return rack;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
