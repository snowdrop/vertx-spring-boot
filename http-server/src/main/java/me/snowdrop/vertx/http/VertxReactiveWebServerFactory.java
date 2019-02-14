package me.snowdrop.vertx.http;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import org.springframework.boot.web.reactive.server.AbstractReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.HttpHandler;

public class VertxReactiveWebServerFactory extends AbstractReactiveWebServerFactory {

    private final Vertx vertx;

    private final NettyDataBufferFactory dataBufferFactory;

    public VertxReactiveWebServerFactory(Vertx vertx, NettyDataBufferFactory dataBufferFactory) {
        this.vertx = vertx;
        this.dataBufferFactory = dataBufferFactory;
    }

    @Override
    public WebServer getWebServer(HttpHandler httpHandler) {
        HttpServerOptions httpServerOptions = new HttpServerOptions();
        if (getPort() > 0) {
            httpServerOptions.setPort(getPort());
        }

        VertxHttpHandlerAdapter handler = new VertxHttpHandlerAdapter(httpHandler, dataBufferFactory);

        return new VertxWebServer(vertx, httpServerOptions, handler);
    }

}
