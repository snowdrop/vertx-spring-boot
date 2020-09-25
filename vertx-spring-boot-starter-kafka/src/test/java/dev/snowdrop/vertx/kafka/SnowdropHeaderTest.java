package dev.snowdrop.vertx.kafka;

import java.nio.charset.StandardCharsets;

import io.vertx.mutiny.kafka.client.producer.KafkaHeader;
import org.junit.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

import static org.assertj.core.api.Assertions.assertThat;


public class SnowdropHeaderTest {

    @Test
    public void shouldCreateHeaderFromDataBuffer() {
        DataBuffer value = new DefaultDataBufferFactory().wrap("value".getBytes(StandardCharsets.UTF_8));
        SnowdropHeader header = new SnowdropHeader("key", value);

        assertThat(header.key()).isEqualTo("key");
        assertThat(header.value()).isEqualTo(value);
    }

    @Test
    public void shouldCreateHeaderFromString() {
        DataBuffer value = new DefaultDataBufferFactory().wrap("value".getBytes(StandardCharsets.UTF_8));
        SnowdropHeader header = new SnowdropHeader("key", "value");

        assertThat(header.key()).isEqualTo("key");
        assertThat(header.value()).isEqualTo(value);
    }

    @Test
    public void shouldCreateHeaderFromMutinyHeader() {
        DataBuffer value = new DefaultDataBufferFactory().wrap("value".getBytes(StandardCharsets.UTF_8));
        SnowdropHeader header = new SnowdropHeader(KafkaHeader.header("key", "value"));

        assertThat(header.key()).isEqualTo("key");
        assertThat(header.value()).isEqualTo(value);
    }
}
