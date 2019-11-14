package dev.snowdrop.vertx.kafka;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

public final class KafkaHeader {

    private final String key;

    private final DataBuffer value;

    public static KafkaHeader create(String key, String value) {
        DataBuffer valueBuffer = new DefaultDataBufferFactory()
            .wrap(value.getBytes(StandardCharsets.UTF_8));

        return create(key, valueBuffer);
    }

    public static KafkaHeader create(String key, DataBuffer value) {
        return new KafkaHeader(key, value);
    }

    static KafkaHeader create(io.vertx.axle.kafka.client.producer.KafkaHeader axelHeader) {
        DataBuffer valueBuffer = new DefaultDataBufferFactory()
            .wrap(axelHeader.value().getBytes());

        return create(axelHeader.key(), valueBuffer);
    }

    private KafkaHeader(String key, DataBuffer value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public DataBuffer getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KafkaHeader that = (KafkaHeader) o;

        return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
