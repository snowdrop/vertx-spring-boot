package dev.snowdrop.vertx;

import io.vertx.core.Vertx;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Vertx.class)
@EnableConfigurationProperties(VertxProperties.class)
public class VertxAutoConfiguration {

    // Let the Vertx user to handle instance closing.
    // This is done in particular for HTTP server which is closed by Spring Context after beans are destroyed.
    // Allowing Vertx bean to be destroyed by the context would block HTTP server from call its close method.
    @Bean(destroyMethod = "")
    public Vertx vertx(VertxProperties properties) {
        return Vertx.vertx(properties.toVertxOptions());
    }
}
