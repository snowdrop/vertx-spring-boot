package dev.snowdrop.vertx.sample.kafka;

import dev.snowdrop.vertx.kafka.KafkaConsumerFactory;
import dev.snowdrop.vertx.kafka.KafkaProducerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KafkaSampleApplication {

    static final String LOG_TOPIC = "log";

    public static void main(String[] args) {
        SpringApplication.run(KafkaSampleApplication.class, args);
    }

    @Bean
    public KafkaLogger kafkaLogger(KafkaProducerFactory producerFactory) {
        return new KafkaLogger(producerFactory.create());
    }

    @Bean
    public KafkaLog kafkaLog(KafkaConsumerFactory consumerFactory) {
        return new KafkaLog(consumerFactory.create());
    }
}
