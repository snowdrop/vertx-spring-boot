package me.snowdrop.vertx.http;

import java.util.LinkedList;
import java.util.List;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import me.snowdrop.vertx.http.properties.HttpServerOptionsCustomizer;
import me.snowdrop.vertx.http.properties.VertxHttpServerProperties;
import org.springframework.boot.web.reactive.server.AbstractReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.HttpHandler;

public class VertxReactiveWebServerFactory extends AbstractReactiveWebServerFactory {

    private final Vertx vertx;

    private final VertxHttpServerProperties vertxHttpServerProperties;

    private final NettyDataBufferFactory dataBufferFactory;

    private final List<HttpServerOptionsCustomizer> httpServerOptionsCustomizers = new LinkedList<>();

    public VertxReactiveWebServerFactory(Vertx vertx, VertxHttpServerProperties vertxHttpServerProperties,
        NettyDataBufferFactory dataBufferFactory) {
        this.vertx = vertx;
        this.vertxHttpServerProperties = vertxHttpServerProperties;
        this.dataBufferFactory = dataBufferFactory;
    }

    @Override
    public WebServer getWebServer(HttpHandler httpHandler) {
        HttpServerOptions httpServerOptions =
            customizeHttpServerOptions(vertxHttpServerProperties.getHttpServerOptions());
        VertxHttpHandlerAdapter handler = new VertxHttpHandlerAdapter(httpHandler, dataBufferFactory);

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
