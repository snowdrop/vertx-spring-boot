package dev.snowdrop.vertx.kafka;

import io.vertx.kafka.client.producer.RecordMetadata;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SnowdropKafkaRecordMetadataTest {

    @Test
    public void shouldCreate() {
        RecordMetadata vertxRecordMetadata = new RecordMetadata(1, 2, 3, 4, "test-topic");

        KafkaRecordMetadata snowdropKafkaRecordMetadata = new SnowdropKafkaRecordMetadata(vertxRecordMetadata);
        assertThat(snowdropKafkaRecordMetadata.getTopic()).isEqualTo("test-topic");
        assertThat(snowdropKafkaRecordMetadata.getPartition()).isEqualTo(3);
        assertThat(snowdropKafkaRecordMetadata.getOffset()).isEqualTo(2);
        assertThat(snowdropKafkaRecordMetadata.getTimestamp()).isEqualTo(4);
        assertThat(snowdropKafkaRecordMetadata.getChecksum()).isEqualTo(1);
    }
}
