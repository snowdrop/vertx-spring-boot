package dev.snowdrop.vertx.kafka;

import java.util.Objects;

class SnowdropTimestampType implements TimestampType {

    private final int id;

    private final String name;

    SnowdropTimestampType(org.apache.kafka.common.record.TimestampType delegate) {
        this.id = delegate.id;
        this.name = delegate.name;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SnowdropTimestampType that = (SnowdropTimestampType) o;
        
        return id == that.id &&
            Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
