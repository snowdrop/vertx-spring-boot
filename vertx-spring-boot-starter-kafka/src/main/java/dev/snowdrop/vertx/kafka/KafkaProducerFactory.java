package dev.snowdrop.vertx.kafka;

import java.util.Collections;
import java.util.Map;

public interface KafkaProducerFactory {

    default <K, V> KafkaProducer<K, V> create() {
        return create(Collections.emptyMap());
    }

    <K, V> KafkaProducer<K, V> create(Map<String, String> config);
}
