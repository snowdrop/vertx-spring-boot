package dev.snowdrop.vertx.kafka;

import java.nio.charset.StandardCharsets;

import io.vertx.axle.core.buffer.Buffer;
import org.junit.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

import static org.assertj.core.api.Assertions.assertThat;


public class KafkaHeaderTest {

    @Test
    public void shouldCreateDataBufferHeader() {
        DataBuffer value = new DefaultDataBufferFactory().wrap("value".getBytes(StandardCharsets.UTF_8));
        KafkaHeader header = KafkaHeader.create("key", value);

        assertThat(header.getKey()).isEqualTo("key");
        assertThat(header.getValue()).isEqualTo(value);
    }

    @Test
    public void shouldCreateStringHeader() {
        DataBuffer value = new DefaultDataBufferFactory().wrap("value".getBytes(StandardCharsets.UTF_8));
        KafkaHeader header = KafkaHeader.create("key", "value");

        assertThat(header.getKey()).isEqualTo("key");
        assertThat(header.getValue()).isEqualTo(value);
    }
}
