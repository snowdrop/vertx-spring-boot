package dev.snowdrop.vertx.kafka.it;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import dev.snowdrop.vertx.kafka.ConsumerRecord;
import dev.snowdrop.vertx.kafka.KafkaConsumer;
import dev.snowdrop.vertx.kafka.KafkaConsumerFactory;
import dev.snowdrop.vertx.kafka.KafkaProducer;
import dev.snowdrop.vertx.kafka.KafkaProducerFactory;
import dev.snowdrop.vertx.kafka.KafkaProperties;
import dev.snowdrop.vertx.kafka.ProducerRecord;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import reactor.core.publisher.Mono;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

public abstract class AbstractIT {

    private KafkaProducerFactory producerFactory;

    private KafkaConsumerFactory consumerFactory;

    private List<KafkaProducer<?, ?>> producersToCleanup = new LinkedList<>();

    private List<KafkaConsumer<?, ?>> consumersToCleanup = new LinkedList<>();

    protected void setUp(KafkaProducerFactory producerFactory, KafkaConsumerFactory consumerFactory,
        KafkaProperties properties, EmbeddedKafkaBroker broker) {

        this.producerFactory = producerFactory;
        this.consumerFactory = consumerFactory;
        properties.setConsumer(addBootstrapServersToConfig(properties.getConsumer(), broker.getBrokersAsString()));
        properties.setProducer(addBootstrapServersToConfig(properties.getProducer(), broker.getBrokersAsString()));
    }

    protected void tearDown() {
        producersToCleanup.stream()
            .map(KafkaProducer::close)
            .forEach(Mono::block);
        consumersToCleanup.stream()
            .map(KafkaConsumer::close)
            .forEach(Mono::block);
    }

    protected <K, V> KafkaProducer<K, V> createProducer() {
        return createProducer(emptyMap());
    }

    protected <K, V> KafkaProducer<K, V> createProducer(Map<String, String> config) {
        KafkaProducer<K, V> producer = producerFactory.create(config);

        // Preserve the producer for cleanup after a test
        producersToCleanup.add(producer);

        return producer;
    }

    protected <K, V> KafkaConsumer<K, V> createConsumer(Map<String, String> config) {
        KafkaConsumer<K, V> consumer = consumerFactory.create(config);

        // Preserve the consumer for cleanup after a test
        consumersToCleanup.add(consumer);

        return consumer;
    }

    protected <K, V> void subscribe(KafkaConsumer<K, V> consumer, String topic, Consumer<ConsumerRecord<K, V>> handler) {
        consumer.flux()
            .log(consumer + " receiving")
            .subscribe(handler);
        consumer
            .subscribe(topic)
            .block();
    }

    protected void waitForAssignmentPropagation() throws InterruptedException {
        // Give Kafka some time to execute partition assignment
        Thread.sleep(2000);
    }

    protected  <K, V> void sendToTopic(KafkaProducer<K, V> producer, String topic, V value) {
        producer
            .send(ProducerRecord.<K, V>builder(topic, value).build())
            .block();
    }

    protected  <K, V> void sendToTopic(KafkaProducer<K, V> producer, String topic, K key, V value) {
        producer
            .send(ProducerRecord.builder(topic, value, key).build())
            .block();
    }

    private Map<String, String> addBootstrapServersToConfig(Map<String, String> config, String bootstrapServers) {
        // Workaround for Spring Kafka 2.2.11. In 2.3.x property can be injected automatically
        config.put("bootstrap.servers", bootstrapServers);

        return config;
    }
}
