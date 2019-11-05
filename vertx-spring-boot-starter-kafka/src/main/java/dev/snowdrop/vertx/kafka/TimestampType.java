package dev.snowdrop.vertx.kafka;

public final class TimestampType {

    private final int id;

    private final String name;

    public TimestampType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
