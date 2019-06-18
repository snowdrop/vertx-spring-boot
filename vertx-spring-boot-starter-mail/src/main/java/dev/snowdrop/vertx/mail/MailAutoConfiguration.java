package dev.snowdrop.vertx.mail;

import io.vertx.core.Vertx;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MailProperties.class)
@ConditionalOnBean(Vertx.class)
@ConditionalOnProperty(prefix = "vertx.mail", value = "enabled", matchIfMissing = true)
public class MailAutoConfiguration {

    @Bean
    public MailClient mailClient(Vertx vertx, MailProperties properties) {
        return new MailClientImpl(getAxleVertx(vertx), getAxleMailClient(vertx, properties));
    }

    private io.vertx.axle.core.Vertx getAxleVertx(Vertx vertx) {
        return new io.vertx.axle.core.Vertx(vertx);
    }

    private io.vertx.axle.ext.mail.MailClient getAxleMailClient(Vertx vertx, MailProperties properties) {
        return io.vertx.axle.ext.mail.MailClient.createNonShared(getAxleVertx(vertx), properties.getMailConfig());
    }
}
