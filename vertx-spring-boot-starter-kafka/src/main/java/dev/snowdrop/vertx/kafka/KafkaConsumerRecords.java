package dev.snowdrop.vertx.kafka;

public interface KafkaConsumerRecords<K, V> {

    int size();

    boolean isEmpty();

    KafkaConsumerRecord<K, V> recordAt(int index);

    org.apache.kafka.clients.consumer.ConsumerRecords<K, V> unwrap();
}
