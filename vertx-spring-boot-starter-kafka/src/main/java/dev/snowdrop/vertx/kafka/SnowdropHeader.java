package dev.snowdrop.vertx.kafka;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.kafka.client.producer.KafkaHeader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.util.StringUtils;

final class SnowdropHeader implements Header {

    private final String key;

    private final DataBuffer value;

    SnowdropHeader(String key, DataBuffer value) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Header key cannot be empty");
        }
        this.key = key;
        this.value = value;
    }

    SnowdropHeader(String key, String value) {
        this(key, toDataBuffer(value));
    }

    SnowdropHeader(KafkaHeader mutinyHeader) {
        this(mutinyHeader.key(), toDataBuffer(mutinyHeader.value()));
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

    private static DataBuffer toDataBuffer(String string) {
        if (string == null) {
            return null;
        }
        return new DefaultDataBufferFactory().wrap(string.getBytes(StandardCharsets.UTF_8));
    }

    private static DataBuffer toDataBuffer(Buffer buffer) {
        if (buffer == null) {
            return null;
        }
        return new DefaultDataBufferFactory().wrap(buffer.getBytes());
    }
}
