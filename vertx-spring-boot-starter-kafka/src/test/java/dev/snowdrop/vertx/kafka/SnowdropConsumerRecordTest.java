package dev.snowdrop.vertx.kafka;

import java.util.Arrays;

import io.vertx.axle.kafka.client.producer.KafkaHeader;
import org.apache.kafka.common.record.TimestampType;
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
        given(mockAxleConsumerRecord.topic()).willReturn("test-topic");
        given(mockAxleConsumerRecord.partition()).willReturn(1);
        given(mockAxleConsumerRecord.offset()).willReturn(2L);
        given(mockAxleConsumerRecord.timestamp()).willReturn(3L);
        given(mockAxleConsumerRecord.timestampType()).willReturn(TimestampType.CREATE_TIME);
        given(mockAxleConsumerRecord.key()).willReturn(4);
        given(mockAxleConsumerRecord.value()).willReturn("test-value");
        given(mockAxleConsumerRecord.headers()).willReturn(Arrays.asList(
            KafkaHeader.header("h1", "v1"),
            KafkaHeader.header("h2", "v2")
        ));
        record = new SnowdropConsumerRecord<>(mockAxleConsumerRecord);
    }

    @Test
    public void shouldGetTopic() {
        assertThat(record.topic()).isEqualTo("test-topic");
    }

    @Test
    public void shouldGetPartition() {
        assertThat(record.partition()).isEqualTo(1);
    }

    @Test
    public void shouldGetOffset() {
        assertThat(record.offset()).isEqualTo(2L);
    }

    @Test
    public void shouldGetTimestamp() {
        assertThat(record.timestamp()).isEqualTo(3L);
    }

    @Test
    public void shouldGetTimestampType() {
        assertThat(record.timestampType())
            .isEqualTo(new SnowdropTimestampType(org.apache.kafka.common.record.TimestampType.CREATE_TIME));
    }

    @Test
    public void shouldGetKey() {
        assertThat(record.key()).isEqualTo(4);
    }

    @Test
    public void shouldGetValue() {
        assertThat(record.value()).isEqualTo("test-value");
    }

    @Test
    public void shouldGetHeaders() {
        assertThat(record.headers()).containsOnly(
            Header.create("h1", "v1"),
            Header.create("h2", "v2")
        );
    }
}
