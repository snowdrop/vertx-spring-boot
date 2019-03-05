package me.snowdrop.vertx.http.properties;

import io.vertx.core.http.HttpServerOptions;
import org.springframework.boot.web.server.Compression;

public class CompressionCustomizer implements HttpServerOptionsCustomizer {

    private final Compression compression;

    public CompressionCustomizer(Compression compression) {
        this.compression = compression;
    }

    @Override
    public HttpServerOptions apply(HttpServerOptions options) {
        return options; // TODO
    }
}
