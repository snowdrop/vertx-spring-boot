package dev.snowdrop.vertx.kafka;

import org.springframework.core.io.buffer.DataBuffer;

public interface Header {

    String key();

    DataBuffer value();

    static Header create(String key, String value) {
        return new SnowdropHeader(key, value);
    }

    static Header create(String key, DataBuffer value) {
        return new SnowdropHeader(key, value);
    }
}
