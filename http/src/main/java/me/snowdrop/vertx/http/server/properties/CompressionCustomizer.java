package me.snowdrop.vertx.http.server.properties;

import io.vertx.core.http.HttpServerOptions;
import org.springframework.boot.web.server.AbstractConfigurableWebServerFactory;
import org.springframework.boot.web.server.Compression;

public class CompressionCustomizer implements HttpServerOptionsCustomizer {

    private final AbstractConfigurableWebServerFactory factory;

    public CompressionCustomizer(AbstractConfigurableWebServerFactory factory) {
        this.factory = factory;
    }

    @Override
    public HttpServerOptions apply(HttpServerOptions options) {
        Compression compression = factory.getCompression();

        if (compression != null) {
            options.setCompressionSupported(compression.getEnabled());
        }

        return options;
    }
}
