package dev.snowdrop.vertx.http.client;

import java.util.Set;

import dev.snowdrop.vertx.http.client.properties.HttpClientOptionsCustomizer;
import dev.snowdrop.vertx.http.client.properties.HttpClientProperties;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnClass({ WebClient.class, HttpClient.class })
@ConditionalOnBean(Vertx.class)
@ConditionalOnMissingBean(ClientHttpConnector.class)
@AutoConfigureBefore(ClientHttpConnectorAutoConfiguration.class)
@EnableConfigurationProperties(HttpClientProperties.class)
public class ClientAutoConfiguration {

    private final HttpClientOptions httpClientOptions;

    public ClientAutoConfiguration(HttpClientProperties properties, Set<HttpClientOptionsCustomizer> customizers) {
        this.httpClientOptions = customizeHttpClientOptions(properties.getHttpClientOptions(), customizers);
    }

    @Bean // TODO implement destroy method
    public VertxClientHttpConnector vertxClientHttpConnector(Vertx vertx) {
        return new VertxClientHttpConnector(vertx, httpClientOptions);
    }

    @Bean // TODO implement destroy method
    public VertxWebSocketClient vertxWebSocketClient(Vertx vertx) {
        return new VertxWebSocketClient(vertx, httpClientOptions);
    }

    private HttpClientOptions customizeHttpClientOptions(HttpClientOptions original,
        Set<HttpClientOptionsCustomizer> customizers) {

        HttpClientOptions customized = new HttpClientOptions(original);

        for (HttpClientOptionsCustomizer customizer : customizers) {
            customized = customizer.apply(customized);
        }

        return customized;
    }
}
