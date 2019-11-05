package dev.snowdrop.vertx.kafka;

import org.springframework.core.io.buffer.DataBuffer;

public interface KafkaHeader {

    String key();

    DataBuffer value();
}
