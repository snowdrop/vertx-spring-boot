package dev.snowdrop.vertx.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SnowdropKafkaConsumerRecordsTest {

    @Mock
    private io.vertx.axle.kafka.client.consumer.KafkaConsumerRecords<Integer, String> mockDelegate;

    private KafkaConsumerRecords<Integer, String> records;

    @Before
    public void setUp() {
        records = new SnowdropKafkaConsumerRecords<>(mockDelegate);
    }

    @Test
    public void shouldGetSize() {
        given(mockDelegate.size()).willReturn(1);

        assertThat(records.size()).isEqualTo(1);
    }

    @Test
    public void shouldNotBeEmpty() {
        given(mockDelegate.isEmpty()).willReturn(true);

        assertThat(records.isEmpty()).isTrue();
    }

    @Test
    public void shouldGetRecord() {
        io.vertx.axle.kafka.client.consumer.KafkaConsumerRecord<Integer, String> axleRecord =
            new io.vertx.axle.kafka.client.consumer.KafkaConsumerRecord<>(
                new io.vertx.kafka.client.consumer.impl.KafkaConsumerRecordImpl<>(
                    new ConsumerRecord<>("test-topic", 1, 2, 3, "test-value")
                )
            );
        given(mockDelegate.recordAt(1)).willReturn(axleRecord);

        assertThat(records.recordAt(1)).isEqualTo(new SnowdropKafkaConsumerRecord<>(axleRecord));
    }
}
