package dev.snowdrop.vertx.mail;

import dev.snowdrop.vertx.mail.impl.EmailServiceImpl;
import io.smallrye.reactive.converters.ReactiveTypeConverter;
import io.smallrye.reactive.converters.Registry;
import io.vertx.axle.ext.mail.MailClient;
import io.vertx.core.Vertx;
import io.vertx.ext.mail.LoginOption;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.StartTLSOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
@EnableConfigurationProperties(MailProperties.class)
@ConditionalOnBean(Vertx.class)
@ConditionalOnClass(MailClient.class)
@ConditionalOnProperty(prefix = "vertx.mail", value = "enabled", matchIfMissing = true)
public class MailAutoConfiguration {

    @Bean
    public MailClient mailClient(Vertx vertx, MailProperties properties) {
        MailConfig config = new MailConfig();
        config
            .setAllowRcptErrors(properties.isAllowRcptErrors())
            .setDisableEsmtp(!properties.isEsmtp())
            .setHostname(properties.getHost())
            .setKeepAlive(properties.isKeepAlive())
            .setPort(properties.getPort())
            .setSsl(properties.isSsl())
            .setTrustAll(properties.isTrustAll());
        properties.getAuthMethods().ifPresent(config::setAuthMethods);
        properties.getKeystore().ifPresent(config::setKeyStore);
        properties.getKeystorePassword().ifPresent(config::setKeyStorePassword);
        properties.getLoginOption().ifPresent(s -> config.setLogin(LoginOption.valueOf(s.toUpperCase())));
        properties.getPassword().ifPresent(config::setPassword);
        properties.getStartTls().ifPresent(s -> config.setStarttls(StartTLSOptions.valueOf(s.toUpperCase())));
        properties.getUsername().ifPresent(config::setUsername);

        return MailClient.createNonShared(new io.vertx.axle.core.Vertx(vertx), config);
    }

    @Bean
    public ReactiveTypeConverter<Mono> monoConverter() {
        return Registry.lookup(Mono.class)
            .orElseThrow(() -> new AssertionError("Mono converter was not found"));
    }

    @Bean
    public EmailService emailService(MailClient mailClient, ReactiveTypeConverter<Mono> monoConverter) {
        return new EmailServiceImpl(mailClient, monoConverter);
    }

}
