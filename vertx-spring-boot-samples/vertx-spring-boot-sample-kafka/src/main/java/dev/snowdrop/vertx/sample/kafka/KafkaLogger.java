package dev.snowdrop.vertx.sample.kafka;

import dev.snowdrop.vertx.kafka.KafkaProducer;
import dev.snowdrop.vertx.kafka.ProducerRecord;
import reactor.core.publisher.Mono;

import static dev.snowdrop.vertx.sample.kafka.KafkaSampleApplication.LOG_TOPIC;

final class KafkaLogger {

    private final KafkaProducer<String, String> producer;

    KafkaLogger(KafkaProducer<String, String> producer) {
        this.producer = producer;
    }

    public Mono<Void> logMessage(String body) {
        // Generic key and value types can be inferred if both key and value are used to create a builder
        ProducerRecord<String, String> record = ProducerRecord.<String, String>builder(LOG_TOPIC, body).build();

        return producer.send(record)
            .log("Kafka logger producer")
            .then();
    }
}
