package me.snowdrop.vertx.http;

import java.util.LinkedList;
import java.util.List;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import me.snowdrop.vertx.http.properties.HttpServerOptionsCustomizer;
import me.snowdrop.vertx.http.properties.HttpServerProperties;
import org.springframework.boot.web.reactive.server.AbstractReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.HttpHandler;

public class VertxReactiveWebServerFactory extends AbstractReactiveWebServerFactory {

    private final Vertx vertx;

    private final HttpServerProperties httpServerProperties;

    private final NettyDataBufferFactory dataBufferFactory;

    private final List<HttpServerOptionsCustomizer> httpServerOptionsCustomizers = new LinkedList<>();

    public VertxReactiveWebServerFactory(Vertx vertx, HttpServerProperties httpServerProperties,
        NettyDataBufferFactory dataBufferFactory) {
        this.vertx = vertx;
        this.httpServerProperties = httpServerProperties;
        this.dataBufferFactory = dataBufferFactory;
    }

    @Override
    public WebServer getWebServer(HttpHandler httpHandler) {
        HttpServerOptions httpServerOptions =
            customizeHttpServerOptions(httpServerProperties.getHttpServerOptions());
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
