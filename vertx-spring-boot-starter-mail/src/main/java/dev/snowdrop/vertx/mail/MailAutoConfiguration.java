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
        io.vertx.axle.core.Vertx axleVertx = new io.vertx.axle.core.Vertx(vertx);
        io.vertx.axle.ext.mail.MailClient axleMailClient =
            io.vertx.axle.ext.mail.MailClient.createNonShared(axleVertx, properties.getMailConfig());

        return new VertxMailClient(axleMailClient, getMailMessageConverter(axleVertx), new MailResultConverter());
    }

    private MailMessageConverter getMailMessageConverter(io.vertx.axle.core.Vertx vertx) {
        MultiMapConverter multiMapConverter = new MultiMapConverter();
        MailAttachmentConverter mailAttachmentConverter = new MailAttachmentConverter(vertx, multiMapConverter);

        return new MailMessageConverter(mailAttachmentConverter, multiMapConverter);
    }
}
