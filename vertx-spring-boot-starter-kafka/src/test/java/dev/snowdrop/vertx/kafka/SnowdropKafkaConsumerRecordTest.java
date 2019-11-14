package dev.snowdrop.vertx.kafka;

import java.util.Arrays;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.record.TimestampType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SnowdropKafkaConsumerRecordTest {

    @Mock
    private ConsumerRecord<Integer, String> mockConsumerRecord;

    @Mock
    private io.vertx.axle.kafka.client.consumer.KafkaConsumerRecord<Integer, String> mockDelegate;

    private KafkaConsumerRecord<Integer, String> record;

    @Before
    public void setUp() {
        record = new SnowdropKafkaConsumerRecord<>(mockDelegate);
    }

    @Test
    public void shouldGetTopic() {
        given(mockDelegate.topic()).willReturn("test-topic");

        assertThat(record.topic()).isEqualTo("test-topic");
    }

    @Test
    public void shouldGetPartition() {
        given(mockDelegate.partition()).willReturn(1);

        assertThat(record.partition()).isEqualTo(1);
    }

    @Test
    public void shouldGetOffset() {
        given(mockDelegate.offset()).willReturn(2L);

        assertThat(record.offset()).isEqualTo(2L);
    }

    @Test
    public void shouldGetTimestamp() {
        given(mockDelegate.timestamp()).willReturn(3L);

        assertThat(record.timestamp()).isEqualTo(3L);
    }

    @Test
    public void shouldGetTimestampType() {
        given(mockDelegate.timestampType()).willReturn(TimestampType.CREATE_TIME);

        assertThat(record.timestampType()).isEqualTo(new SnowdropKafkaTimestampType(TimestampType.CREATE_TIME));
    }

    @Test
    public void shouldGetKey() {
        given(mockDelegate.key()).willReturn(4);

        assertThat(record.key()).isEqualTo(4);
    }

    @Test
    public void shouldGetValue() {
        given(mockDelegate.value()).willReturn("test-value");

        assertThat(record.value()).isEqualTo("test-value");
    }

    @Test
    public void shouldGetHeaders() {
        List<io.vertx.axle.kafka.client.producer.KafkaHeader> axleHeaders = Arrays.asList(
            io.vertx.axle.kafka.client.producer.KafkaHeader.header("h1", "v1"),
            io.vertx.axle.kafka.client.producer.KafkaHeader.header("h2", "v2")
        );
        given(mockDelegate.headers()).willReturn(axleHeaders);

        assertThat(record.headers()).containsOnly(
            KafkaHeader.create("h1", "v1"),
            KafkaHeader.create("h2", "v2")
        );
    }
}
