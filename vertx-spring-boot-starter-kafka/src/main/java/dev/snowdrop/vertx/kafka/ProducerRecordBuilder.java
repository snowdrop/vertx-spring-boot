package dev.snowdrop.vertx.kafka;

import java.util.List;

public interface ProducerRecordBuilder<T extends ProducerRecord<K, V>, K, V> {

    ProducerRecordBuilder<T, K, V> withKey(K key);

    ProducerRecordBuilder<T, K, V> withPartition(int partition);

    ProducerRecordBuilder<T, K, V> withTimestamp(long timestamp);

    ProducerRecordBuilder<T, K, V> withHeader(Header header);

    ProducerRecordBuilder<T, K, V> withHeaders(List<Header> headers);

    T build();
}
