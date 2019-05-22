package dev.snowdrop.vertx.http.test;

import java.util.Collection;
import java.util.function.Supplier;

import dev.snowdrop.vertx.http.client.VertxClientHttpConnector;
import dev.snowdrop.vertx.http.server.VertxReactiveWebServerFactory;
import dev.snowdrop.vertx.http.server.properties.HttpServerProperties;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

public class VertxWebTestClientSupplier implements Supplier<WebTestClient> {

    private final ApplicationContext applicationContext;

    private WebTestClient webTestClient;

    public VertxWebTestClientSupplier(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public WebTestClient get() {
        if (webTestClient == null) {
            webTestClient = createWebTestClient();
        }

        return webTestClient;
    }

    private WebTestClient createWebTestClient() {
        VertxClientHttpConnector connector = applicationContext.getBean(VertxClientHttpConnector.class);

        WebTestClient.Builder builder = WebTestClient.bindToServer(connector);

        String baseUrl = String.format("%s://localhost:%s", getProtocol(), getPort());
        builder.baseUrl(baseUrl);

        customizeWebTestClientCodecs(builder);

        return builder.build();
    }

    private String getProtocol() {
        VertxReactiveWebServerFactory factory = applicationContext.getBean(VertxReactiveWebServerFactory.class);

        boolean isSsl;

        if (factory.getSsl() != null) {
            isSsl = factory.getSsl().isEnabled();
        } else {
            HttpServerProperties serverProperties = applicationContext.getBean(HttpServerProperties.class);
            isSsl = serverProperties.isSsl();
        }

        return isSsl ? "https" : "http";
    }

    private String getPort() {
        return applicationContext.getEnvironment().getProperty("local.server.port", "8080");
    }

    private void customizeWebTestClientCodecs(WebTestClient.Builder builder) {
        Collection<CodecCustomizer> customizers = applicationContext.getBeansOfType(CodecCustomizer.class).values();

        ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(codecs -> customizers.forEach(customizer -> customizer.customize(codecs)))
            .build();

        builder.exchangeStrategies(strategies);
    }
}
