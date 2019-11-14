package dev.snowdrop.vertx.kafka;

public interface KafkaConsumerRecords<K, V> {

    int size();

    boolean isEmpty();

    KafkaConsumerRecord<K, V> recordAt(int index);
}
