package dev.snowdrop.vertx.http.server;

import java.util.LinkedList;
import java.util.List;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import dev.snowdrop.vertx.http.server.properties.HttpServerOptionsCustomizer;
import dev.snowdrop.vertx.http.server.properties.HttpServerProperties;
import org.springframework.boot.web.reactive.server.AbstractReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.http.server.reactive.HttpHandler;

public class VertxReactiveWebServerFactory extends AbstractReactiveWebServerFactory {

    private final Vertx vertx;

    private final HttpServerProperties properties;

    private final List<HttpServerOptionsCustomizer> httpServerOptionsCustomizers = new LinkedList<>();

    public VertxReactiveWebServerFactory(Vertx vertx, HttpServerProperties properties) {
        this.vertx = vertx;
        this.properties = properties;
    }

    @Override
    public WebServer getWebServer(HttpHandler httpHandler) {
        HttpServerOptions httpServerOptions = customizeHttpServerOptions(properties.getHttpServerOptions());
        VertxHttpHandlerAdapter handler = new VertxHttpHandlerAdapter(httpHandler);

        return new VertxWebServer(vertx, httpServerOptions, handler);
    }

    public void registerHttpServerOptionsCustomizer(HttpServerOptionsCustomizer customizer) {
        httpServerOptionsCustomizers.add(customizer);
    }

    private HttpServerOptions customizeHttpServerOptions(HttpServerOptions httpServerOptions) {
        for (HttpServerOptionsCustomizer customizer : httpServerOptionsCustomizers) {
            httpServerOptions = customizer.apply(httpServerOptions);
        }
        return httpServerOptions;
    }
}
