package dev.snowdrop.vertx.sample.kafka;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import dev.snowdrop.vertx.kafka.ConsumerRecord;
import dev.snowdrop.vertx.kafka.KafkaConsumer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import reactor.core.Disposable;

import static dev.snowdrop.vertx.sample.kafka.KafkaSampleApplication.LOG_TOPIC;

final class KafkaLog implements InitializingBean, DisposableBean {

    private final List<String> messages = new CopyOnWriteArrayList<>();

    private final KafkaConsumer<String, String> consumer;

    private Disposable consumerDisposer;

    KafkaLog(KafkaConsumer<String, String> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void afterPropertiesSet() {
        consumerDisposer = consumer.subscribe(LOG_TOPIC)
            .thenMany(consumer.flux())
            .log("Kafka log consumer")
            .map(ConsumerRecord::value)
            .subscribe(messages::add);
    }

    @Override
    public void destroy() {
        if (consumerDisposer != null) {
            consumerDisposer.dispose();
        }
        consumer.unsubscribe()
            .block(Duration.ofSeconds(2));
    }

    public List<String> getMessages() {
        return messages;
    }
}
