package dev.snowdrop.vertx.postgres;

public class ColumnValue {

    private final String name;

    private final Object value;

    public ColumnValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
