package dev.snowdrop.vertx.kafka;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = KafkaProperties.PROPERTIES_PREFIX)
public final class KafkaProperties {

    static final String PROPERTIES_PREFIX = "vertx.kafka";

    /**
     * Enable Kafka starter.
     */
    private boolean enabled = true;

    /**
     * Consumer specific properties.
     *
     * @see org.apache.kafka.clients.consumer.ConsumerConfig
     */
    private Map<String, String> consumer = new HashMap<>();

    /**
     * Producer specific properties.
     *
     * @see org.apache.kafka.clients.producer.ProducerConfig
     */
    private Map<String, String> producer = new HashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, String> getConsumer() {
        return new HashMap<>(consumer);
    }

    public void setConsumer(Map<String, String> consumer) {
        this.consumer = new HashMap<>(consumer);
    }

    public Map<String, String> getProducer() {
        return new HashMap<>(producer);
    }

    public void setProducer(Map<String, String> producer) {
        this.producer = new HashMap<>(producer);
    }
}
