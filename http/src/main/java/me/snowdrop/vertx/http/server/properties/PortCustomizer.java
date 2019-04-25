package me.snowdrop.vertx.http.server.properties;

import io.vertx.core.http.HttpServerOptions;

public class PortCustomizer implements HttpServerOptionsCustomizer {

    private final int port;

    public PortCustomizer(int port) {
        this.port = port;
    }

    @Override
    public HttpServerOptions apply(HttpServerOptions options) {
        if (port >= 0) {
            options.setPort(port);
        }

        return options;
    }

}
