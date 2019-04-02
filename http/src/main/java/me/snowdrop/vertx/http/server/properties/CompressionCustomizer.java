package me.snowdrop.vertx.http.server.properties;

import io.vertx.core.http.HttpServerOptions;
import org.springframework.boot.web.server.Compression;

public class CompressionCustomizer implements HttpServerOptionsCustomizer {

    private final Compression compression;

    public CompressionCustomizer(Compression compression) {
        this.compression = compression;
    }

    @Override
    public HttpServerOptions apply(HttpServerOptions options) {
        if (compression == null) {
            return options;
        }

        // Only enabled property is applicable for HttpServerOptions
        return options.setCompressionSupported(compression.getEnabled());
    }
}
