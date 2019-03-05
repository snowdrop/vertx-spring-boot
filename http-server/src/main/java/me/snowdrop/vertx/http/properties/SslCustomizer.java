package me.snowdrop.vertx.http.properties;

import io.vertx.core.http.HttpServerOptions;
import org.springframework.boot.web.server.Ssl;

public class SslCustomizer implements HttpServerOptionsCustomizer {

    private final Ssl ssl;

    public SslCustomizer(Ssl ssl) {
        this.ssl = ssl;
    }

    @Override
    public HttpServerOptions apply(HttpServerOptions options) {
        return options; // TODO
    }
}
