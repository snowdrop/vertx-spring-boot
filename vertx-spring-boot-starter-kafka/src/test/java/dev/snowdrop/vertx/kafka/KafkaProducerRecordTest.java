package dev.snowdrop.vertx.kafka;

import java.util.Collections;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KafkaProducerRecordTest {

    @Test
    public void shouldCreateRecordWithDefaultValues() {
        KafkaProducerRecord<Integer, String> record = KafkaProducerRecord
            .builder("test-topic", "test-value", Integer.class)
            .build();

        assertThat(record.getTopic()).isEqualTo("test-topic");
        assertThat(record.getValue()).isEqualTo("test-value");
        assertThat(record.getKey()).isNull();
        assertThat(record.getTimestamp()).isNull();
        assertThat(record.getPartition()).isNull();
        assertThat(record.getHeaders()).isEmpty();
    }

    @Test
    public void shouldCreateRecordWithProvidedValues() {
        KafkaProducerRecord<Integer, String> record = KafkaProducerRecord
            .builder("test-topic", "test-value", Integer.class)
            .withKey(1)
            .withTimestamp(2)
            .withPartition(3)
            .withHeader(KafkaHeader.create("key1", "value1"))
            .withHeaders(Collections.singletonList(KafkaHeader.create("key2", "value2")))
            .build();

        assertThat(record.getTopic()).isEqualTo("test-topic");
        assertThat(record.getValue()).isEqualTo("test-value");
        assertThat(record.getKey()).isEqualTo(1);
        assertThat(record.getTimestamp()).isEqualTo(2);
        assertThat(record.getPartition()).isEqualTo(3);
        assertThat(record.getHeaders())
            .containsOnly(KafkaHeader.create("key1", "value1"), KafkaHeader.create("key2", "value2"));
    }
}
