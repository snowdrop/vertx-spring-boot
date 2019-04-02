package me.snowdrop.vertx.http;

import java.util.Set;

import io.vertx.core.Vertx;
import me.snowdrop.vertx.http.properties.HttpServerOptionsCustomizer;
import me.snowdrop.vertx.http.properties.HttpServerProperties;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(ReactiveHttpInputMessage.class)
@ConditionalOnMissingBean(ReactiveWebServerFactory.class)
@EnableConfigurationProperties(HttpServerProperties.class)
public class HttpServerAutoConfiguration {

    private final BufferConverter bufferConverter = new BufferConverter();

    @Bean
    public VertxReactiveWebServerFactory vertxReactiveWebServerFactory(Vertx vertx, HttpServerProperties properties) {
        return new VertxReactiveWebServerFactory(vertx, properties, bufferConverter);
    }

    @Bean
    public VertxReactiveWebServerFactoryCustomizer vertxWebServerFactoryCustomizer(
        Set<HttpServerOptionsCustomizer> userDefinedCustomizers) {
        return new VertxReactiveWebServerFactoryCustomizer(userDefinedCustomizers);
    }

    @Bean
    public WebSocketService webSocketService() {
        VertxRequestUpgradeStrategy requestUpgradeStrategy = new VertxRequestUpgradeStrategy(bufferConverter);
        return new HandshakeWebSocketService(requestUpgradeStrategy);
    }

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter(WebSocketService webSocketService) {
        return new WebSocketHandlerAdapter(webSocketService);
    }
}
