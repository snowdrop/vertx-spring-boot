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
        io.vertx.mutiny.core.Vertx mutinyVertx = new io.vertx.mutiny.core.Vertx(vertx);
        io.vertx.mutiny.ext.mail.MailClient mutinyMailClient =
            io.vertx.mutiny.ext.mail.MailClient.create(mutinyVertx, properties.getMailConfig());

        return new VertxMailClient(mutinyMailClient, getMailMessageConverter(mutinyVertx), new MailResultConverter());
    }

    private MailMessageConverter getMailMessageConverter(io.vertx.mutiny.core.Vertx vertx) {
        MultiMapConverter multiMapConverter = new MultiMapConverter();
        MailAttachmentConverter mailAttachmentConverter = new MailAttachmentConverter(vertx, multiMapConverter);

        return new MailMessageConverter(mailAttachmentConverter, multiMapConverter);
    }
}
