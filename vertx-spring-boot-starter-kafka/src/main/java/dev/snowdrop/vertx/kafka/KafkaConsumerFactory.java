package dev.snowdrop.vertx.kafka;

import java.util.Collections;
import java.util.Map;

public interface KafkaConsumerFactory {

    default <K, V> KafkaConsumer<K, V> create() {
        return create(Collections.emptyMap());
    }

    <K, V> KafkaConsumer<K, V> create(Map<String, String> config);
}
