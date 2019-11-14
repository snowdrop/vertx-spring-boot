package dev.snowdrop.vertx.kafka;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import io.vertx.axle.kafka.client.producer.KafkaHeader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

final class SnowdropHeader implements Header {

    private final String key;

    private final DataBuffer value;

    SnowdropHeader(String key, DataBuffer value) {
        this.key = key;
        this.value = value;
    }

    SnowdropHeader(String key, String value) {
        this(key, toDataBuffer(value.getBytes(StandardCharsets.UTF_8)));
    }

    SnowdropHeader(KafkaHeader axelHeader) {
        this(axelHeader.key(), toDataBuffer(axelHeader.value().getBytes()));
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public DataBuffer value() {
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

        SnowdropHeader that = (SnowdropHeader) o;

        return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    private static DataBuffer toDataBuffer(byte[] bytes) {
        return new DefaultDataBufferFactory().wrap(bytes);
    }
}
