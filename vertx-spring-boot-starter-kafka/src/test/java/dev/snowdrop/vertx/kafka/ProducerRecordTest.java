package dev.snowdrop.vertx.kafka;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProducerRecordTest {

    @Test
    public void shouldCreateRecordWithDefaultValues() {
        ProducerRecord<Integer, String> record = ProducerRecord
            .<Integer, String>builder("test-topic", "test-value")
            .build();

        assertThat(record.topic()).isEqualTo("test-topic");
        assertThat(record.value()).isEqualTo("test-value");
        assertThat(record.key()).isNull();
        assertThat(record.timestamp()).isNull();
        assertThat(record.partition()).isNull();
        assertThat(record.headers()).isEmpty();
    }

    @Test
    public void shouldCreateRecordWithProvidedValues() {
        ProducerRecord<Integer, String> record = ProducerRecord
            .builder("test-topic", "test-value", 1)
            .withTimestamp(2)
            .withPartition(3)
            .withHeader(Header.create("key1", "value1"))
            .withHeaders(Collections.singletonList(Header.create("key2", "value2")))
            .build();

        assertThat(record.topic()).isEqualTo("test-topic");
        assertThat(record.value()).isEqualTo("test-value");
        assertThat(record.key()).isEqualTo(1);
        assertThat(record.timestamp()).isEqualTo(2);
        assertThat(record.partition()).isEqualTo(3);
        assertThat(record.headers())
            .containsOnly(Header.create("key1", "value1"), Header.create("key2", "value2"));
    }
}
