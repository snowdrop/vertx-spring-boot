package dev.snowdrop.vertx.kafka;

import io.vertx.core.Vertx;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@ConditionalOnBean(Vertx.class)
@ConditionalOnProperty(prefix = KafkaProperties.PROPERTIES_PREFIX, value = "enabled", matchIfMissing = true)
public class KafkaAutoConfiguration {

    @Bean
    public KafkaProducerFactory kafkaProducerFactory(KafkaProperties properties, Vertx vertx) {
        return new SnowdropKafkaProducerFactory(io.vertx.axle.core.Vertx.newInstance(vertx), properties);
    }

    @Bean
    public KafkaConsumerFactory kafkaConsumerFactory(KafkaProperties properties, Vertx vertx) {
        return new SnowdropKafkaConsumerFactory(io.vertx.axle.core.Vertx.newInstance(vertx), properties);
    }
}
