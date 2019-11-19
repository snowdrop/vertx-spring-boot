package dev.snowdrop.vertx.kafka;

import java.util.Map;

import io.vertx.axle.core.Vertx;

final class SnowdropKafkaConsumerFactory implements KafkaConsumerFactory {

    private final Vertx vertx;

    private final KafkaProperties properties;

    SnowdropKafkaConsumerFactory(Vertx vertx, KafkaProperties properties) {
        this.vertx = vertx;
        this.properties = properties;
    }

    @Override
    public <K, V> KafkaConsumer<K, V> create(Map<String, String> config) {
        Map<String, String> consumerConfig = properties.getConsumer();
        consumerConfig.putAll(config);

        return new SnowdropKafkaConsumer<>(
            io.vertx.axle.kafka.client.consumer.KafkaConsumer.create(vertx, consumerConfig));
    }
}
