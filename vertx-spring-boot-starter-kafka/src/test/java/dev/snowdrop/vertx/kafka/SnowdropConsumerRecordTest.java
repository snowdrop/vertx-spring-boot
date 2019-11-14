package dev.snowdrop.vertx.kafka;

import java.util.Arrays;
import java.util.List;

import io.vertx.axle.kafka.client.producer.KafkaHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SnowdropConsumerRecordTest {

    @Mock
    private org.apache.kafka.clients.consumer.ConsumerRecord mockKafkaConsumerRecord;

    @Mock
    private io.vertx.axle.kafka.client.consumer.KafkaConsumerRecord<Integer, String> mockAxleConsumerRecord;

    private ConsumerRecord<Integer, String> record;

    @Before
    public void setUp() {
        record = new SnowdropConsumerRecord<>(mockAxleConsumerRecord);
    }

    @Test
    public void shouldGetTopic() {
        given(mockAxleConsumerRecord.topic()).willReturn("test-topic");

        assertThat(record.topic()).isEqualTo("test-topic");
    }

    @Test
    public void shouldGetPartition() {
        given(mockAxleConsumerRecord.partition()).willReturn(1);

        assertThat(record.partition()).isEqualTo(1);
    }

    @Test
    public void shouldGetOffset() {
        given(mockAxleConsumerRecord.offset()).willReturn(2L);

        assertThat(record.offset()).isEqualTo(2L);
    }

    @Test
    public void shouldGetTimestamp() {
        given(mockAxleConsumerRecord.timestamp()).willReturn(3L);

        assertThat(record.timestamp()).isEqualTo(3L);
    }

    @Test
    public void shouldGetTimestampType() {
        given(mockAxleConsumerRecord.timestampType())
            .willReturn(org.apache.kafka.common.record.TimestampType.CREATE_TIME);

        assertThat(record.timestampType())
            .isEqualTo(new SnowdropTimestampType(org.apache.kafka.common.record.TimestampType.CREATE_TIME));
    }

    @Test
    public void shouldGetKey() {
        given(mockAxleConsumerRecord.key()).willReturn(4);

        assertThat(record.key()).isEqualTo(4);
    }

    @Test
    public void shouldGetValue() {
        given(mockAxleConsumerRecord.value()).willReturn("test-value");

        assertThat(record.value()).isEqualTo("test-value");
    }

    @Test
    public void shouldGetHeaders() {
        List<KafkaHeader> axleHeaders = Arrays.asList(
            KafkaHeader.header("h1", "v1"),
            KafkaHeader.header("h2", "v2")
        );
        given(mockAxleConsumerRecord.headers()).willReturn(axleHeaders);

        assertThat(record.headers()).containsOnly(
            Header.create("h1", "v1"),
            Header.create("h2", "v2")
        );
    }
}
