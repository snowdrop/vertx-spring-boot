package dev.snowdrop.vertx.kafka;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SnowdropRecordMetadataTest {

    @Test
    public void shouldCreate() {
        io.vertx.kafka.client.producer.RecordMetadata
            vertxRecordMetadata = new io.vertx.kafka.client.producer.RecordMetadata(1, 2, 3, 4, "test-topic");

        RecordMetadata snowdropRecordMetadata = new SnowdropRecordMetadata(vertxRecordMetadata);
        assertThat(snowdropRecordMetadata.topic()).isEqualTo("test-topic");
        assertThat(snowdropRecordMetadata.partition()).isEqualTo(3);
        assertThat(snowdropRecordMetadata.offset()).isEqualTo(2);
        assertThat(snowdropRecordMetadata.timestamp()).isEqualTo(4);
        assertThat(snowdropRecordMetadata.checksum()).isEqualTo(1);
    }
}
