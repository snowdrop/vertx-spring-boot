package dev.snowdrop.vertx.amqp;

import io.vertx.amqp.AmqpClientOptions;
import io.vertx.core.Vertx;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AmqpProperties.class)
@ConditionalOnBean(Vertx.class)
@ConditionalOnProperty(prefix = AmqpProperties.PROPERTIES_PREFIX, value = "enabled", matchIfMissing = true)
public class AmqpAutoConfiguration {

    @Bean
    public AmqpClient amqpClient(Vertx vertx, AmqpProperties properties) {
        AmqpPropertiesConverter propertiesConverter = new AmqpPropertiesConverter();
        AmqpClientOptions options = propertiesConverter.toAmqpClientOptions(properties);

        return new SnowdropAmqpClient(getAxleAmqpClient(vertx, options), new MessageConverter());
    }

    private io.vertx.axle.core.Vertx getAxleVertx(Vertx vertx) {
        return new io.vertx.axle.core.Vertx(vertx);
    }

    private io.vertx.axle.amqp.AmqpClient getAxleAmqpClient(Vertx vertx, AmqpClientOptions options) {
        return io.vertx.axle.amqp.AmqpClient.create(getAxleVertx(vertx), options);
    }
}
