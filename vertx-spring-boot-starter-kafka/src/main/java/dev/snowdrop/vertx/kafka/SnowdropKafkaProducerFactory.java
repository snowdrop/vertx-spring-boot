package dev.snowdrop.vertx.kafka;

import java.util.Map;

import io.vertx.axle.core.Vertx;

final class SnowdropKafkaProducerFactory implements KafkaProducerFactory {

    private final Vertx vertx;

    private final KafkaProperties properties;

    SnowdropKafkaProducerFactory(Vertx vertx, KafkaProperties properties) {
        this.vertx = vertx;
        this.properties = properties;
    }

    @Override
    public <K, V> KafkaProducer<K, V> create(Map<String, String> config) {
        Map<String, String> producerConfig = properties.getProducer();
        producerConfig.putAll(config);

        return new SnowdropKafkaProducer<>(
            io.vertx.axle.kafka.client.producer.KafkaProducer.create(vertx, producerConfig));
    }
}
